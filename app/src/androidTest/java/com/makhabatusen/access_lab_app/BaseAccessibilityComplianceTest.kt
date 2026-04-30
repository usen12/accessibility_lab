package com.makhabatusen.access_lab_app

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertTextContains
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Shared data class for EN 301 549 test results. Top-level so it can be
 * imported by test classes in any sub-package without qualification.
 */
data class TestResult(
    val testName: String,
    val criterion: String,
    val description: String,
    val passed: Boolean,
    val details: String,
    val errorMessage: String? = null,
    val recommendation: String? = null,
    val executionTime: Long = 0
)

/**
 * Abstract base class that provides shared infrastructure for all EN 301 549
 * accessibility compliance tests:
 * - TestResult storage and helpers
 * - runTest() wrapper (records result, re-throws on failure)
 * - recordTestResult() for manual result recording
 * - getString() resource helper
 * - SemanticsNodeInteraction extension functions
 * - Report file generation to external storage
 */
abstract class BaseAccessibilityComplianceTest {

    abstract val componentName: String

    protected val context
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    protected val testResults = mutableListOf<TestResult>()
    protected var startTime = System.currentTimeMillis()

    protected fun resetResults() {
        testResults.clear()
        startTime = System.currentTimeMillis()
    }

    protected fun runTest(
        testName: String,
        criterion: String,
        description: String,
        testBlock: () -> Unit
    ) {
        val testStartTime = System.currentTimeMillis()
        var passed = false
        var errorMessage: String? = null

        try {
            testBlock()
            passed = true
        } catch (e: AssertionError) {
            errorMessage = e.message
            throw e
        } catch (e: Exception) {
            errorMessage = e.message
            throw e
        } finally {
            val executionTime = System.currentTimeMillis() - testStartTime
            testResults.add(
                TestResult(
                    testName = testName,
                    criterion = criterion,
                    description = description,
                    passed = passed,
                    details = if (passed) "Test executed successfully" else "Test failed",
                    errorMessage = errorMessage,
                    recommendation = if (!passed) "Review implementation and ensure proper semantics are set" else null,
                    executionTime = executionTime
                )
            )
            val status = if (passed) "✅ PASS" else "❌ FAIL"
            println("$status $testName (${System.currentTimeMillis() - testStartTime}ms)")
            if (!passed) println("   Error: $errorMessage")
        }
    }

    protected fun recordTestResult(
        testName: String,
        criterion: String,
        description: String,
        passed: Boolean,
        details: String,
        errorMessage: String? = null,
        recommendation: String? = null,
        executionTime: Long = 0
    ) {
        testResults.add(
            TestResult(testName, criterion, description, passed, details, errorMessage, recommendation, executionTime)
        )
    }

    protected fun getString(resourceId: Int): String =
        try {
            context.getString(resourceId)
        } catch (e: Exception) {
            throw IllegalStateException("String resource $resourceId not found: ${e.message}")
        }

    protected fun getString(resourceId: Int, vararg formatArgs: Any): String =
        try {
            context.getString(resourceId, *formatArgs)
        } catch (e: Exception) {
            throw IllegalStateException("String resource $resourceId not found: ${e.message}")
        }

    protected fun SemanticsNodeInteraction.assertHasSemanticsProperty(
        property: SemanticsPropertyKey<*>,
        error: String
    ) {
        val semantics = this.fetchSemanticsNode()
        assert(semantics.config.contains(property)) { error }
    }

    protected fun SemanticsNodeInteraction.assertHasContentDescription(error: String) =
        assertHasSemanticsProperty(SemanticsProperties.ContentDescription, error)

    protected fun SemanticsNodeInteraction.assertHasStateDescription(error: String) =
        assertHasSemanticsProperty(SemanticsProperties.StateDescription, error)

    protected fun SemanticsNodeInteraction.assertHasRole(expectedRole: Role, error: String) {
        val semantics = this.fetchSemanticsNode()
        val actualRole = semantics.config.getOrNull(SemanticsProperties.Role)
        assert(actualRole == expectedRole) { error }
    }

    protected fun SemanticsNodeInteraction.assertTextFromResource(resourceId: Int, _error: String) {
        this.assertTextContains(getString(resourceId))
    }

    protected fun generateAccessibilityReports() {
        if (testResults.isEmpty()) return
        try {
            val reportsDir = File(context.getExternalFilesDir(null), "accessibility_reports")
            reportsDir.mkdirs()
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val prefix = componentName.lowercase().replace(" ", "_")
            File(reportsDir, "${prefix}_report_$timestamp.txt").writeText(buildTextReport())
            File(reportsDir, "${prefix}_report_$timestamp.json").writeText(buildJsonReport())
            println("📊 Accessibility reports saved to: ${reportsDir.absolutePath}")
        } catch (e: Exception) {
            println("❌ Error saving accessibility report: ${e.message}")
        }
    }

    private fun buildTextReport(): String {
        val sb = StringBuilder()
        val totalTime = System.currentTimeMillis() - startTime
        val passed = testResults.count { it.passed }
        val total = testResults.size
        val rate = if (total > 0) passed * 100.0 / total else 0.0

        sb.appendLine("EN 301 549 ACCESSIBILITY COMPLIANCE REPORT — $componentName")
        sb.appendLine("=".repeat(70))
        sb.appendLine("Generated: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}")
        sb.appendLine("Total: $total | Passed: $passed | Failed: ${total - passed} | Rate: ${String.format("%.1f", rate)}% | Time: ${totalTime}ms")
        sb.appendLine()

        testResults.groupBy { it.criterion }.forEach { (criterion, results) ->
            val cPassed = results.count { it.passed }
            sb.appendLine("CRITERION $criterion ($cPassed/${results.size}):")
            results.forEach { r ->
                sb.appendLine("  ${if (r.passed) "✅" else "❌"} ${r.testName} (${r.executionTime}ms)")
                if (!r.passed) sb.appendLine("    Error: ${r.errorMessage}")
            }
            sb.appendLine()
        }

        val failed = testResults.filter { !it.passed }
        if (failed.isNotEmpty()) {
            sb.appendLine("RECOMMENDATIONS:")
            failed.forEach { r ->
                sb.appendLine("• ${r.testName}: ${r.recommendation ?: "Review implementation"}")
            }
        } else {
            sb.appendLine("✅ All tests passed. $componentName appears compliant with EN 301 549.")
        }

        sb.appendLine("=".repeat(70))
        return sb.toString()
    }

    private fun buildJsonReport(): String {
        val total = testResults.size
        val passed = testResults.count { it.passed }
        val rate = if (total > 0) passed * 100.0 / total else 0.0
        val sb = StringBuilder()
        sb.appendLine("{")
        sb.appendLine("  \"component\": \"$componentName\",")
        sb.appendLine("  \"generated\": \"${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}\",")
        sb.appendLine("  \"summary\": { \"total\": $total, \"passed\": $passed, \"failed\": ${total - passed}, \"complianceRate\": $rate },")
        sb.appendLine("  \"results\": [")
        testResults.forEachIndexed { i, r ->
            val comma = if (i < testResults.size - 1) "," else ""
            sb.appendLine("    { \"testName\": \"${r.testName}\", \"criterion\": \"${r.criterion}\", \"passed\": ${r.passed}, \"executionTime\": ${r.executionTime}, \"errorMessage\": ${if (r.errorMessage != null) "\"${r.errorMessage}\"" else "null"} }$comma")
        }
        sb.appendLine("  ]")
        sb.appendLine("}")
        return sb.toString()
    }
}
