import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

import com.bstek.bdf2.dms.client.AbstractDocumentServiceRequest;
import com.bstek.bdf2.dms.client.RopCompositeResponse;
import com.bstek.bdf2.dms.client.impl.DefaultRopClient;
import com.bstek.bdf2.dms.client.impl.DocumentServiceDeleteRequest;
import com.bstek.bdf2.dms.client.impl.DocumentServiceDownloadRequest;
import com.bstek.bdf2.dms.client.impl.DocumentServiceDownloadResponse;
import com.bstek.bdf2.dms.client.impl.DocumentServiceResponse;
import com.bstek.bdf2.dms.client.impl.DocumentServiceUploadRequest;
import com.rop.MessageFormat;
import com.rop.request.UploadFile;
import com.rop.response.ErrorResponse;
import com.rop.security.SubError;

public class DmsRestTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ShaPasswordEncoder md = new ShaPasswordEncoder();
		System.out.println(md.encodePassword("12345678", "34"));

		String serverURL = "http://localhost:8080/bdf2-dms/document/router";
		String appkey = "dmsApp001";
		String secret = "app001Secret123";
		String user = "dmsAdmin";
		String pwd = "f9186bcc0808244a444b77b7a5c693312bae27f9";
		// System.out.println("484fe69-6d68-4486-obdbc-b8de37b0ca07".length());
		// Pattern p = Pattern.compile("[\\w-]{30,60}");
		// Matcher m = p.matcher("e484fe69-6d68-4486-bdbc-b8de37b0ca07");
		// System.out.println(m.find());
		DefaultRopClient drc = new DefaultRopClient(serverURL, appkey, secret,
				MessageFormat.json);
		uploadRequest(user, pwd, drc);
		// downloadRequest(user, pwd, drc);
	}

	@SuppressWarnings("unused")
	private static void uploadRequest(String user, String pwd,
			DefaultRopClient drc) {
		DocumentServiceUploadRequest request = new DocumentServiceUploadRequest();
		request.setUserName(user);
		request.setPassword(pwd);
		request.setFolderPath("e83b22a7-eea5-46f9-a296-20bf59985bdc");
		File file = new File(System.getProperty("user.dir") + File.separator
				+ "src" + File.separator + "1215高技能鉴定JAVA考前辅导.rar");
		request.setFileName(file.getName());
		UploadFile uploadDocument = new UploadFile(file);
		request.setUploadDocument(uploadDocument);
		RopCompositeResponse<?> res = drc.post(
				AbstractDocumentServiceRequest.METHOD_1_UPLOAD_,
				AbstractDocumentServiceRequest.METHOD_VERSION_1_, request,
				DocumentServiceResponse.class, null);
		if (res.isSuccess()) {
			DocumentServiceResponse dsRes = (DocumentServiceResponse) res
					.getSuccessfulResponse();
			System.out.println(dsRes.isProcessFlag());
			System.out.println(dsRes.getMessage());
		} else {
			ErrorResponse errRes = res.getErrorResponse();
			System.out.println(errRes.getCode());
			System.out.println(errRes.getMessage());
			System.out.println(errRes.getSolution());
			List<SubError> subEs = errRes.getSubErrors();
			if (subEs != null)
				for (SubError se : subEs) {
					System.out.println(se.getCode() + "\n" + se.getMessage());
				}
		}
	}

	private static void downloadRequest(String user, String pwd,
			DefaultRopClient drc) {
		DocumentServiceDownloadRequest request = new DocumentServiceDownloadRequest();
		request.setUserName(user);
		request.setPassword(pwd);
		request.setDocumentUUID("639c771c-ed4f-461a-9bd3-3e15ccdd8446");
		RopCompositeResponse<?> res = drc.get(
				AbstractDocumentServiceRequest.METHOD_2_DOWNLOAD_,
				AbstractDocumentServiceRequest.METHOD_VERSION_1_, request,
				DocumentServiceDownloadResponse.class, null);
		if (res.isSuccess()) {
			DocumentServiceDownloadResponse dsRes = (DocumentServiceDownloadResponse) res
					.getSuccessfulResponse();
			File file = new File(System.getProperty("user.dir")
					+ File.separator + "src" + File.separator
					+ dsRes.getDocumentName());
			try {
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(dsRes.getContent());
				fos.close();
				System.out.println(dsRes.getDocumentName());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			ErrorResponse errRes = res.getErrorResponse();
			System.out.println(errRes.getCode());
			System.out.println(errRes.getMessage());
			System.out.println(errRes.getSolution());
			List<SubError> subEs = errRes.getSubErrors();
			if (subEs != null)
				for (SubError se : subEs) {
					System.out.println(se.getCode() + "\n" + se.getMessage());
				}
		}
	}

	@SuppressWarnings("unused")
	private static void deleteDocument(String user, String pwd,
			DefaultRopClient drc) {
		DocumentServiceDeleteRequest request = new DocumentServiceDeleteRequest();
		request.setUserName(user);
		request.setPassword(pwd);
		request.setDocumentUUID("c17e3795-d9d0-447d-bbe2-5f4e955b92df");
		RopCompositeResponse<?> res = drc.get(
				AbstractDocumentServiceRequest.METHOD_3_DELETE_,
				AbstractDocumentServiceRequest.METHOD_VERSION_1_, request,
				DocumentServiceResponse.class, null);
		if (res.isSuccess()) {
			DocumentServiceResponse dsRes = (DocumentServiceResponse) res
					.getSuccessfulResponse();
			System.out.println(dsRes.isProcessFlag());
			System.out.println(dsRes.getMessage());
		} else {
			ErrorResponse errRes = res.getErrorResponse();
			System.out.println(errRes.getCode());
			System.out.println(errRes.getMessage());
			System.out.println(errRes.getSolution());
			List<SubError> subEs = errRes.getSubErrors();
			if (subEs != null)
				for (SubError se : subEs) {
					System.out.println(se.getCode() + "\n" + se.getMessage());
				}
		}
	}
}
