package multithreading;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownLoaderMain {

	public static void main(String[] args) throws MalformedURLException

	{

		DownLoader d1 = new DownLoader("https://www.tutorialspoint.com/java/java_tutorial.pdf", "D:/download");
		DownLoader d2 = new DownLoader("https://www.tutorialspoint.com/java/java_tutorial.pdf", "D:/download");
		DownLoader d3 = new DownLoader("https://www.tutorialspoint.com/java/java_tutorial.pdf", "D:/download");
		DownLoader d4 = new DownLoader("https://www.tutorialspoint.com/java/java_tutorial.pdf", "D:/download");

		d1.start();
		d2.start();
		d3.start();
		d4.start();

	}
}

class DownLoader extends Thread {

	private String fileURL;
	private String saveDir;

	public DownLoader(String fileURL, String saveDir) {
		super();
		this.fileURL = fileURL;
		this.saveDir = saveDir;
	}

	@Override
	public void run() {
	
		FileDownloader fileDownloader = new FileDownloader();
		try {
			
			fileDownloader.downloadmethod(fileURL, saveDir);
			//Thread.sleep(1000);
			System.out.println(Thread.currentThread());

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

}

class FileDownloader {

	public void downloadmethod(String fileURL, String saveDir) throws IOException {

		final int BUFFER_SIZE = 2048;

		URL url = new URL(fileURL);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		int responseCode = httpConn.getResponseCode();

		// always check HTTP response code first
		if (responseCode == HttpURLConnection.HTTP_OK) {
			String fileName = "";
			String disposition = httpConn.getHeaderField("Content-Disposition");
			String contentType = httpConn.getContentType();
			int contentLength = httpConn.getContentLength();

			if (disposition != null) {
				// extracts file name from header field
				int index = disposition.indexOf("filename=");
				if (index > 0) {
					fileName = disposition.substring(index + 10, disposition.length() - 1);
				}
			} else {
				// extracts file name from URL
				fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
			}

			System.out.println("Content-Type = " + contentType);
			System.out.println("Content-Disposition = " + disposition);
			System.out.println("Content-Length = " + contentLength);
			System.out.println("fileName = " + fileName);

			// opens input stream from the HTTP connection
			InputStream inputStream = httpConn.getInputStream();
			String saveFilePath = saveDir + File.separator + fileName;

			// opens an output stream to save into file
			FileOutputStream outputStream = new FileOutputStream(saveFilePath);

			int bytesRead = -1;
			byte[] buffer = new byte[BUFFER_SIZE];
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}

			outputStream.close();
			inputStream.close();

			System.out.println("File downloaded");
		} else {
			System.out.println("No file to download. Server replied HTTP code: " + responseCode);
		}
		httpConn.disconnect();
	}
}
