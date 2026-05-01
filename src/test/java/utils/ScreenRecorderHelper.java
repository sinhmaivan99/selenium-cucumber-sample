package utils;

import io.qameta.allure.Attachment;
import org.monte.media.Format;
import org.monte.media.FormatKeys;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.monte.media.FormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

public class ScreenRecorderHelper {

    private static final String SCREENSHOT_DIR = "target/screenshots";
    private static final String VIDEO_DIR = "target/videos";

    private static final ThreadLocal<ScreenRecorder> screenRecorder = new ThreadLocal<>();
    private static final ThreadLocal<String> currentVideoName = new ThreadLocal<>();

    // ===================== SCREENSHOT =====================

    @Attachment(value = "Screenshot: {testName}", type = "image/png")
    public static byte[] captureScreenshot(WebDriver driver, String testName) {
        LogHelper.info("Taking screenshot for: " + testName);
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    public static String saveScreenshotToFile(WebDriver driver, String testName) {
        try {
            createDirectory(SCREENSHOT_DIR);

            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            String fileName = buildFileName(testName, "png");
            Path filePath = Paths.get(SCREENSHOT_DIR, fileName);
            Files.write(filePath, screenshot);

            return filePath.toAbsolutePath().toString();
        } catch (IOException e) {
            LogHelper.error("Failed to save screenshot: " + testName, e);
            throw new RuntimeException("Failed to save screenshot: " + testName, e);
        }
    }

    // ===================== VIDEO RECORDING =====================

    public static void startRecording(String testName) {
        try {
            createDirectory(VIDEO_DIR);

            File videoDir = new File(VIDEO_DIR);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Rectangle captureArea = new Rectangle(0, 0, screenSize.width, screenSize.height);

            Format fileFormat = new Format(
                    MediaTypeKey, FormatKeys.MediaType.FILE,
                    MimeTypeKey, MIME_AVI
            );

            Format screenFormat = new Format(
                    MediaTypeKey, FormatKeys.MediaType.VIDEO,
                    EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                    CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                    DepthKey, 24,
                    FrameRateKey, Rational.valueOf(15),
                    QualityKey, 1.0f,
                    KeyFrameIntervalKey, 15 * 60
            );

            Format mouseFormat = new Format(
                    MediaTypeKey, FormatKeys.MediaType.VIDEO,
                    EncodingKey, "black",
                    FrameRateKey, Rational.valueOf(30)
            );

            ScreenRecorder recorder = new ScreenRecorder(
                    GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration(),
                    captureArea,
                    fileFormat,
                    screenFormat,
                    mouseFormat,
                    null,
                    videoDir
            );

            recorder.start();
            screenRecorder.set(recorder);
            String newFileName = buildFileName(testName, "avi");
            currentVideoName.set(newFileName);
            LogHelper.info("Started video recording: " + newFileName);

        } catch (Exception e) {
            LogHelper.error("Failed to start recording: " + testName, e);
        }
    }

    public static void stopRecording(String testName, boolean isFailed) {
        try {
            ScreenRecorder recorder = screenRecorder.get();
            if (recorder == null) {
                return;
            }

            recorder.stop();

            List<File> createdFiles = recorder.getCreatedMovieFiles();
            if (createdFiles.isEmpty()) {
                return;
            }

            File recordedFile = createdFiles.get(createdFiles.size() - 1);
            String targetFileName = currentVideoName.get();
            File renamedFile = new File(VIDEO_DIR, targetFileName);

            if (recordedFile.renameTo(renamedFile)) {
                if (isFailed) {
                    attachVideoToAllure(renamedFile);
                } else {
                    // Xóa video nếu test pass để tiết kiệm dung lượng
                    if (renamedFile.exists()) {
                        renamedFile.delete();
                    }
                }
            } else {
                 if (isFailed) {
                     attachVideoToAllure(recordedFile);
                 } else {
                     if (recordedFile.exists()) {
                         recordedFile.delete();
                     }
                 }
            }
        } catch (Exception e) {
            LogHelper.error("Failed to stop recording: " + testName, e);
        } finally {
            screenRecorder.remove();
            currentVideoName.remove();
        }
    }

    @Attachment(value = "Video on Failure", type = "video/avi")
    private static byte[] attachVideoToAllure(File videoFile) {
        try {
            if (videoFile != null && videoFile.exists()) {
                return Files.readAllBytes(videoFile.toPath());
            }
            return new byte[0];
        } catch (IOException e) {
            LogHelper.error("Failed to attach video to Allure", e);
            return new byte[0];
        }
    }

    // ===================== UTILITY =====================

    private static void createDirectory(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private static String buildFileName(String testName, String extension) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return testName + "_" + timestamp + "." + extension;
    }
}
