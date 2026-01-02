import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import com.kms.katalon.core.annotation.AfterTestSuite
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.context.TestSuiteContext
import com.kms.katalon.core.logging.KeywordLogger
import com.kms.katalon.core.logging.model.TestSuiteLogRecord
import com.kms.katalon.core.reporting.ReportWriterUtil
import com.kms.katalon.core.reporting.ReportWriterUtil.SuiteReportGenerationOptionsBuilder
import com.kms.katalon.core.reporting.pdf.TestSuitePdfGenerator
import com.kms.katalon.core.reporting.pdf.exception.JasperReportException
import com.kms.katalon.core.setting.ReportBundleSettingStore
import com.kms.katalon.core.setting.ReportSettings
import com.kms.katalon.core.util.internal.ExceptionsUtil


class GenerateTestSuiteReports {
	
	@AfterTestSuite
	def afterTestSuite(TestSuiteContext testSuiteContext) {
		// generate test-suite-report.html file
		Path htmlReport = Generator.generateHTMLReport("test-suite-report.html")
		println "HTML report: " + htmlReport.toString()
		
		// generate test-suite-report.pdf file
		Path pdfReport = Generator.generatePDFReport("test-suite-report.pdf")
		println "PDF report: " + pdfReport.toString()
		
	}
	
	/**
	 * 
	 */
	private class Generator {
		
		private static final KeywordLogger logger = KeywordLogger.getInstance(Generator.class);
		
		private static Path projectDir = Paths.get(RunConfiguration.getProjectDir()).toAbsolutePath()
		private static Path reportFolder = Paths.get(RunConfiguration.getReportFolder()).toAbsolutePath()
		private static ReportSettings settings = ReportBundleSettingStore.getStore(projectDir.toString()).getSettings()
		
		static void validateParams() {
			assert Files.exists(projectDir)
			assert settings != null
			assert Files.exists(reportFolder)
		}
		
		static Path generateHTMLReport(String htmlFileName) {
			validateParams()
			Path htmlReport = reportFolder.resolve(htmlFileName).toAbsolutePath()
			TestSuiteLogRecord testSuiteLogRecord = ReportWriterUtil.parseTestSuiteLog(reportFolder.toString())
			ReportWriterUtil.writeHTMLReport(
				SuiteReportGenerationOptionsBuilder.create()
					.suiteLogRecord(testSuiteLogRecord)
					.settings(settings)
					.reportDir(reportFolder.toFile())
					.outputFile(htmlReport.toFile())
					.build())
			return htmlReport
		}
		
		static Path generatePDFReport(String pdfFileName) {
			validateParams()
			Path pdfReport = reportFolder.resolve(pdfFileName).toAbsolutePath()
			TestSuiteLogRecord testSuiteLogRecord = ReportWriterUtil.parseTestSuiteLog(reportFolder.toString())
			TestSuitePdfGenerator pdfGenerator = new TestSuitePdfGenerator(testSuiteLogRecord);
			try {
				pdfGenerator.exportToPDF(pdfReport.toString());
			} catch (JasperReportException e) {
				logger.logWarning(ExceptionsUtil.getStackTraceForThrowable(e));
				throw new IOException(e);
			}
			return pdfReport
		}
	}
}