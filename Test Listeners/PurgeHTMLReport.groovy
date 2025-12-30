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