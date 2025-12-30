# Purging HTML Report in the afterTestSuite() in Test Listener

This is a small Katalon Studio project for demonstration purpose.
This project proposes a solution to the problem raised at the topic in the Katalon Communiti:

- https://forum.katalon.com/t/manual-generate-html-report/185508

The original poster argued:

>zedromason
>Nov 12
>Hello, is there a setting to generate an HTML file so it can be accessed immediately before the test script is finished?
>I’m having trouble. I manually integrated Jira with a listener and uploaded supporting documents to Jira. The only files my script can upload are .png and console0, but the HTML file isn’t found because it only appears after the script finishes running.

## Problem to solve

Katalon Studio generates HTML report after the Test Suite is entirely finished; after the `@AfterTestSuite`-annotated method in Test Listener finished. But I want to generate the HTML HTML report in the `@AfterTestSuite`-annotated method and post-process the file further (e.g., upload to JIRA).

## Solution

A `@AfterTestSuite`-annotated methid in my Test Listener should invoke the processing of generating the HTML report. I would not rely on Katalon Studio to generate it. I will do it myself.

## Solution Description

I developed a Test Listener `Test Listeners/PurgeHTMLReport.groovy`, which is short as this:

```
import java.nio.file.Path
import java.nio.file.Paths

import com.kms.katalon.core.annotation.AfterTestSuite
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.context.TestSuiteContext
import com.kms.katalon.core.logging.model.TestSuiteLogRecord
import com.kms.katalon.core.reporting.ReportWriterUtil
import com.kms.katalon.core.reporting.ReportWriterUtil.SuiteReportGenerationOptionsBuilder
import com.kms.katalon.core.setting.ReportBundleSettingStore

class PurgeHTMLReport {

	@AfterTestSuite
	def afterTestSuite(TestSuiteContext testSuiteContext) {
		String projectDir = RunConfiguration.getProjectDir()
		Path reportFolder = Paths.get(RunConfiguration.getReportFolder())
		Path exportLocation = reportFolder.resolve("test-suite-report.html")
		File report = exportTestSuite(exportLocation.toFile(), projectDir, reportFolder.toFile())
		println "HTML report: " + report.toString()
	}

	/**
	 * quoted from com.kms.katalon.core.reporting.KatalonExportReportProvider#exportTestSuite() with slight modifications
	 */
	File exportTestSuite(File exportLocation, String projectDir, File reportFolder) {
		def settings = ReportBundleSettingStore.getStore(projectDir).getSettings()
		assert reportFolder.exists()
		TestSuiteLogRecord testSuiteLogRecord =
			ReportWriterUtil.parseTestSuiteLog(reportFolder.getAbsolutePath())
		ReportWriterUtil.writeHTMLReport(
			SuiteReportGenerationOptionsBuilder.create()
				.suiteLogRecord(testSuiteLogRecord)
				.settings(settings)
				.reportDir(reportFolder)
				.outputFile(exportLocation)
				.build())
		return exportLocation
	}
}
```

With this Test Listener implemented, I ran a Test Suite. I could see the Report folder contained `test-suite-report.html`:




Navigate to https://docs.katalon.com/katalon-studio/docs/health-care-prj.html for further details.
