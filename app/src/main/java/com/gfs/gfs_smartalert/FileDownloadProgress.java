package com.gfs.gfs_smartalert;

public class FileDownloadProgress {
    private double downloadTotalBytes; // 다운로드 파일 용량
    private double downloadReceivedBytes; // 다운로드 받아진 파일 용량

    /**
     * 전체 파일 용량과 받은 파일 용량을 저장
     *
     * @param totalBytes    전체 파일 용량
     * @param receivedBytes 받은 파일 용량
     */
    public FileDownloadProgress(long totalBytes, long receivedBytes) {
        downloadTotalBytes = totalBytes;
        downloadReceivedBytes = receivedBytes;
    }

    /**
     * 현재 다운로드 중인 파일용량 중
     * 다운로드 받은 파일용량을 퍼센트로 계산
     * @return 다운로드 퍼센트
     */
    public double progressPercent() {
        double totalBytes = downloadTotalBytes;
        double receivedBytes = downloadReceivedBytes;
        return (receivedBytes / totalBytes);
    }
}
