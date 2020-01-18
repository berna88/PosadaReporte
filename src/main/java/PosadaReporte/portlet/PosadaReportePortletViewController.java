package PosadaReporte.portlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
//import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.util.JRLoader;
/**
 * @author liferay
 */
@Controller
@RequestMapping("VIEW")
public class PosadaReportePortletViewController {

	@RenderMapping
	public String view(RenderRequest request, RenderResponse response) {
		
		List<JournalArticle> articles = JournalArticleLocalServiceUtil.getArticles();
		
		for(JournalArticle art: articles){
			System.out.println(art.getTitle() + " : "
					+ art.getVersion() + " : "+ 
					art.getUserName() + " : " +
					art.getModifiedDate());
		}
		
		return "view";
	}
	
	@ResourceMapping(value = "showReport")
	public void executeImprimePostit(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws PortalException, JRException, IOException {

	ThemeDisplay themeDisplay = (ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);

	Folder folder = DLAppServiceUtil.getFolder(themeDisplay.getScopeGroupId(),
	DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Reportes");

	DLFileEntry fileEntry = null;


	long fileEntryId1 = 0;


	List<DLFileEntry> fileEntries = DLFileEntryLocalServiceUtil.getFileEntries(folder.getRepositoryId(),
	folder.getFolderId());

	for (DLFileEntry m : fileEntries) {

	System.out.println(m.getFolderId() + " " + m.getFileName());

	if (m.getFileName().equals("ReportePosadas.jasper")) {
	fileEntryId1 = m.getFileEntryId();
	break;
	} 
	}

	fileEntry = DLFileEntryLocalServiceUtil.getFileEntry(fileEntryId1);

	Map<String, Object> parameters = new HashMap<String, Object>();

	parameters.put("identificador", 1);
	parameters.put("titulo", "Sample Report");
	parameters.put("version", "1.0");
	String url = themeDisplay.getPortalURL() + themeDisplay.getPathContext() + "/documents/"
	+ themeDisplay.getScopeGroupId() + "/" + fileEntry.getFolderId() + "/" + fileEntry.getTitle();

	InputStream reporte = JRLoader.getURLInputStream(url);// getFileInputStream(fileJasper.getAbsolutePath());

	byte[] reportePDF = JasperRunManager.runReportToPdf(reporte, parameters, new JREmptyDataSource());

	if (reportePDF.length > 0) {
	resourceResponse.reset();
	resourceResponse.resetBuffer();
	resourceResponse.setContentType("application/pdf");
	resourceResponse.setContentLength(reportePDF.length);
	OutputStream ouputStream = resourceResponse.getPortletOutputStream();
	ouputStream.write(reportePDF, 0, reportePDF.length);
	ouputStream.flush();
	ouputStream.close();
	}
	}
}