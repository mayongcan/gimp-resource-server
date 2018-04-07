package com.gimplatform.resserver.restful;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.gimplatform.resserver.common.FileUtils;

/**
 * 试题分类
 * @author zzd
 */
@RestController
@RequestMapping(value = "/res/download")
public class DownloadRestful {

    protected static final Logger logger = LogManager.getLogger(DownloadRestful.class);

    @Value("${resourceServer.uploadFilePath}")
    private String uploadFilePath;

    /**
     * 获取资源文件真实路径
     * @return
     */
    @RequestMapping(value="/getFilePath",method=RequestMethod.POST)
    public String getFilePath(){
        return uploadFilePath;
    }

    /**
     * 下载文件，将文件打包成zip下载
     * @param file
     * @return
     */
    @RequestMapping(value = "/downloadFile", method = RequestMethod.GET)
    public void downloadFile(HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, Object> params) {
        try {
            String files = MapUtils.getString(params, "files");
            if (StringUtils.isBlank(files))  return;
            String arrayFiles[] = files.split(",");
            // 多个文件，打包成为zip下载
            OutputStream out = setDownloadOutputStream(response, "download_" + String.valueOf(new Date().getTime()), "zip");
            ZipOutputStream zipOut = new ZipOutputStream(out);
            for (int i = 0; i < arrayFiles.length; i++) {
                File tmpFile = new File(uploadFilePath + arrayFiles[i]);
                byte[] data = FileUtils.toByte(new FileInputStream(tmpFile));
                zipOut.putNextEntry(new ZipEntry(tmpFile.getName()));
                writeBytesToOut(zipOut, data, 4096);
                zipOut.closeEntry();
            }
            zipOut.flush();
            zipOut.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
    
    /**
     * 设置文件下载的response格式
     *
     * @param response 响应
     * @param fileName 文件名称
     * @param fileType 文件类型
     * @return 设置后响应的输出流OutputStream
     * @throws IOException
     */
    private static OutputStream setDownloadOutputStream(HttpServletResponse response, String fileName, String fileType) throws IOException {
        fileName = new String(fileName.getBytes(), "ISO-8859-1");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + "." + fileType);
        response.setContentType("multipart/form-data");
        return response.getOutputStream();
    }
    
    /**
     * 将byte[]类型的数据，写入到输出流中
     *
     * @param out 输出流
     * @param data 希望写入的数据
     * @param cacheSize 写入数据是循环读取写入的，此为每次读取的大小，单位字节，建议为4096，即4k
     * @throws IOException
     */
    private static void writeBytesToOut(OutputStream out, byte[] data, int cacheSize) throws IOException {
        int surplus = data.length % cacheSize;
        int count = surplus == 0 ? data.length / cacheSize : data.length / cacheSize + 1;
        for (int i = 0; i < count; i++) {
            if (i == count - 1 && surplus != 0) {
                out.write(data, i * cacheSize, surplus);
                continue;
            }
            out.write(data, i * cacheSize, cacheSize);
        }
    }
}
