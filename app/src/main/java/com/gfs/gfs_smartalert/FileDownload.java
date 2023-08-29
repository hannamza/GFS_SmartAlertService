package com.gfs.gfs_smartalert;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FileDownload {
    private static final String DOWNLOAD_HEADER_KEY = "Accept-Encoding"; // HTTP 헤더 요청 타입
    private static final String DOWNLOAD_HEADER_VALUE = "identity"; // HTTP 헤더 요청 값
    private static final int DOWNLOAD_BUFFER_SIZE = 1024 * 256;

    /**
     * 파일 다운로드를 실행
     *
     * @param downloadUrlStr   다운로드 URL
     * @param destinationPath  저장 경로
     * @param progressCallback 프로그레스 콜백
     * @return 다운로드 성공 여부
     */
    public String download(String downloadUrlStr, String destinationPath, FileDownloadProgressCallback progressCallback) {
        String result = "000"; // 다운로드 성공, 실패 여부 저장

        HttpURLConnection connection = null;
        BufferedInputStream bin = null;
        FileOutputStream fos = null;
        BufferedOutputStream bout = null;
        File downloadFile;

        try {
            URL downloadUrl = new URL(downloadUrlStr);
            connection = (HttpURLConnection) (downloadUrl.openConnection());
            connection.setRequestProperty(DOWNLOAD_HEADER_KEY, DOWNLOAD_HEADER_VALUE);
            bin = new BufferedInputStream(connection.getInputStream());

            long totalBytes = connection.getContentLength(); // 전체파일 크기
            long receivedBytes = 0; // 다운로드 받은 파일 크기
            File downloadFolder = new File(destinationPath);
            if (!downloadFolder.exists()) {
                downloadFolder.mkdirs();
            }
            downloadUrlStr = downloadUrlStr.substring(downloadUrlStr.lastIndexOf("/") + 1);
            downloadFile = new File(downloadFolder, downloadUrlStr);

            fos = new FileOutputStream(downloadFile);
            bout = new BufferedOutputStream(fos, DOWNLOAD_BUFFER_SIZE);
            byte[] data = new byte[DOWNLOAD_BUFFER_SIZE];
            byte[] header = new byte[4];

            int numBytesRead;
            try {
                while ((numBytesRead = bin.read(data, 0, DOWNLOAD_BUFFER_SIZE)) >= 0) {
                    if (receivedBytes < 4) {
                        for (int i = 0; i < numBytesRead; i++) {
                            int headerOffset = (int) (receivedBytes) + i;
                            if (headerOffset >= 4) {
                                break;
                            }
                            header[headerOffset] = data[i];
                        }
                    }
                    receivedBytes += numBytesRead;
                    bout.write(data, 0, numBytesRead);

                    // 프로그레스 바가 있다면 갱신해줄수 있다.
                    progressCallback.call(new FileDownloadProgress(totalBytes, receivedBytes));
                }
            } catch (InterruptedIOException i) {
                // 다운로드 중 멈춤
                result = "999";
            }
            if (totalBytes != receivedBytes) {
                // "전체 파일 다운 실패 : Received " + receivedBytes + " bytes, expected " + totalBytes);
                result = "999";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace(); // http 명시 안함
            // 다운로드 URL 이 올바르지 않습니다.
            result = "999";
        } catch (FileNotFoundException e) {
            e.printStackTrace(); // 파일 찾을 수 없음
            // URL 에서 파일을 찾을 수 없습니다.
            result = "999";
        } catch (IOException e) { // 파일 I/O 오류
            e.printStackTrace();
            // 파일 다운로드 오류 발생.
            result = "999";
        } finally {
            try {
                if (bout != null) bout.close();
                if (fos != null) fos.close();
                if (bin != null) bin.close();
                if (connection != null) connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                // 파일 다운로드 종료 오류 발생.
                result = "999";
            }
        }

        return result;
    }
}
