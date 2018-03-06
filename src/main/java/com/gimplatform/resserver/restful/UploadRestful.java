package com.gimplatform.resserver.restful;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONObject;

/**
 * 试题分类
 * @author zzd
 */
@RestController
@RequestMapping(value = "/res/upload")
public class UploadRestful {

    protected static final Logger logger = LogManager.getLogger(UploadRestful.class);

    @Value("${resourceServer.uploadFilePath}")
    private String uploadFilePath;

    /**
     * 上传图片文件
     * @param file
     * @return
     */
    @RequestMapping("/uploadImage")
    @ResponseBody
    public JSONObject uploadImage(@RequestParam("file") MultipartFile file) {
        JSONObject json = new JSONObject();
        if (!file.isEmpty()) {
            try {
                String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                // UUID生成文件名
                String fileName = UUID.randomUUID().toString() + suffix;
                // 创建年月日的目录
                String filePath = "Image/" + formatDate(new Date(), "yyyy") + "/" + formatDate(new Date(), "MM") + "/" + formatDate(new Date(), "dd") + "/";
                createDirectory(uploadFilePath + filePath);
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(uploadFilePath + filePath + fileName)));
                out.write(file.getBytes());
                out.flush();
                out.close();
                // 返回文件名
                json = RestfulRetUtils.getRetSuccess(filePath + fileName);
            } catch (FileNotFoundException e) {
                logger.error(e.getMessage(), e);
                json = RestfulRetUtils.getErrorMsg("10000", "文件上传失败：" + e.getMessage());
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                json = RestfulRetUtils.getErrorMsg("10001", "文件上传失败：" + e.getMessage());
            }
            return json;
        } else {
            json = RestfulRetUtils.getErrorMsg("10002", "文件上传失败：获取上传文件失败");
            return json;
        }
    }

    /**
     * 上传图片文件
     * @param file
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("/uploadFile")
    @ResponseBody
    public JSONObject uploadFile(HttpServletRequest request) throws UnsupportedEncodingException {
        JSONObject json = new JSONObject();
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        List<MultipartFile> list = multipartHttpServletRequest.getFiles("file");
        if (list != null && list.size() > 0) {
            // if(!file.isEmpty()){
            try {
                MultipartFile file = list.get(0);
                String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                String fileWithoutSuffix = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));
                // 创建年月日的目录
                String filePath = "File/" + formatDate(new Date(), "yyyy") + "/" + formatDate(new Date(), "MM") + "/" + formatDate(new Date(), "dd") + "/";
                createDirectory(uploadFilePath + filePath);
                String fileName = "";
                String modifyName = request.getParameter("modifyName");
                if (StringUtils.isBlank(modifyName)) {
                    // UUID生成文件名
                    fileName = UUID.randomUUID().toString() + suffix;
                } else {
                    // -1则不修改文件名
                    if ("-1".equals(modifyName)) {
                        fileName = file.getOriginalFilename();
                        // 判断文件是否存在
                        File tmpFile = new File(uploadFilePath + filePath + fileName);
                        if (tmpFile.exists()) {
                            // 文件按存在，则增加年月日时分秒
                            fileName = fileWithoutSuffix + "_" + formatDate(new Date(), "yyyy") + formatDate(new Date(), "MM") + formatDate(new Date(), "dd") + formatDate(new Date(), "HH") + formatDate(new Date(), "mm") + formatDate(new Date(), "ss")
                                    + suffix;
                        }
                    } else {
                        // 自定义文件名
                        fileName = modifyName + suffix;
                        // 判断文件是否存在
                        File tmpFile = new File(uploadFilePath + filePath + fileName);
                        if (tmpFile.exists()) {
                            // 文件按存在，则增加年月日时分秒
                            fileName = modifyName + "_" + formatDate(new Date(), "yyyy") + formatDate(new Date(), "MM") + formatDate(new Date(), "dd") + formatDate(new Date(), "HH") + formatDate(new Date(), "mm") + formatDate(new Date(), "ss")
                                    + suffix;
                        }
                    }
                }
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(uploadFilePath + filePath + fileName)));
                out.write(file.getBytes());
                out.flush();
                out.close();
                // 返回文件名
                json = RestfulRetUtils.getRetSuccess(filePath + fileName);
            } catch (FileNotFoundException e) {
                logger.error(e.getMessage(), e);
                json = RestfulRetUtils.getErrorMsg("10000", "文件上传失败：" + e.getMessage());
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                json = RestfulRetUtils.getErrorMsg("10001", "文件上传失败：" + e.getMessage());
            }
            return json;
        } else {
            json = RestfulRetUtils.getErrorMsg("10002", "文件上传失败：获取上传文件失败");
            return json;
        }
    }

    /**
     * 上传多个文件
     * @param file
     * @return
     */
    @RequestMapping("/uploadMultiFile")
    @ResponseBody
    public JSONObject uploadMultiFile(HttpServletRequest request) {
        JSONObject json = new JSONObject();
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        MultipartFile file = null;
        StringBuffer sb = new StringBuffer();
        try {
            for (int i = 0; i < files.size(); i++) {
                file = files.get(i);
                if (!file.isEmpty()) {
                    String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                    String fileWithoutSuffix = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));
                    // 创建年月日的目录
                    String filePath = "File/" + formatDate(new Date(), "yyyy") + "/" + formatDate(new Date(), "MM") + "/" + formatDate(new Date(), "dd") + "/";
                    createDirectory(uploadFilePath + filePath);
                    String fileName = "";
                    String modifyName = request.getParameter("modifyName");
                    if (StringUtils.isBlank(modifyName)) {
                        // UUID生成文件名
                        fileName = UUID.randomUUID().toString() + suffix;
                    } else {
                        // -1则不修改文件名
                        if ("-1".equals(modifyName)) {
                            fileName = file.getOriginalFilename();
                            // 判断文件是否存在
                            File tmpFile = new File(uploadFilePath + filePath + fileName);
                            if (tmpFile.exists()) {
                                // 文件按存在，则增加年月日时分秒
                                fileName = fileWithoutSuffix + "_" + formatDate(new Date(), "yyyy") + formatDate(new Date(), "MM") + formatDate(new Date(), "dd") + formatDate(new Date(), "HH") + formatDate(new Date(), "mm")
                                        + formatDate(new Date(), "ss") + suffix;
                            }
                        } else {
                            // 自定义文件名
                            fileName = modifyName + suffix;
                            // 判断文件是否存在
                            File tmpFile = new File(uploadFilePath + filePath + fileName);
                            if (tmpFile.exists()) {
                                // 文件按存在，则增加年月日时分秒
                                fileName = modifyName + "_" + formatDate(new Date(), "yyyy") + formatDate(new Date(), "MM") + formatDate(new Date(), "dd") + formatDate(new Date(), "HH") + formatDate(new Date(), "mm") + formatDate(new Date(), "ss")
                                        + suffix;
                            }
                        }
                    }
                    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(uploadFilePath + filePath + fileName)));
                    out.write(file.getBytes());
                    out.flush();
                    out.close();
                    sb.append(filePath + fileName);
                    sb.append(",");
                } else {
                    json = RestfulRetUtils.getErrorMsg("10002", "文件上传失败：获取上传文件失败");
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            json = RestfulRetUtils.getErrorMsg("10000", "文件上传失败：" + e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            json = RestfulRetUtils.getErrorMsg("10001", "文件上传失败：" + e.getMessage());
        }
        // 返回文件名
        if (sb.length() == 0)
            json = RestfulRetUtils.getErrorMsg("10002", "文件上传失败：获取上传文件失败");
        else
            json = RestfulRetUtils.getRetSuccess(sb.substring(0, sb.length() - 1));
        return json;
    }

    /**
     * 上传图片文件
     * @param file
     * @return
     */
    @RequestMapping("/uploadMultiImage")
    @ResponseBody
    public JSONObject uploadMultiImage(HttpServletRequest request) {
        JSONObject json = new JSONObject();
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        MultipartFile file = null;
        StringBuffer sb = new StringBuffer();
        try {
            for (int i = 0; i < files.size(); i++) {
                file = files.get(i);
                if (!file.isEmpty()) {
                    String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                    // UUID生成文件名
                    String fileName = UUID.randomUUID().toString() + suffix;
                    // 创建年月日的目录
                    String filePath = "Image/" + formatDate(new Date(), "yyyy") + "/" + formatDate(new Date(), "MM") + "/" + formatDate(new Date(), "dd") + "/";
                    createDirectory(uploadFilePath + filePath);
                    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(uploadFilePath + filePath + fileName)));
                    out.write(file.getBytes());
                    out.flush();
                    out.close();
                    sb.append(filePath + fileName);
                    sb.append(",");
                } else {
                    json = RestfulRetUtils.getErrorMsg("10002", "文件上传失败：获取上传文件失败");
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            json = RestfulRetUtils.getErrorMsg("10000", "文件上传失败：" + e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            json = RestfulRetUtils.getErrorMsg("10001", "文件上传失败：" + e.getMessage());
        }
        // 返回文件名
        if (sb.length() == 0)
            json = RestfulRetUtils.getErrorMsg("10002", "文件上传失败：获取上传文件失败");
        else
            json = RestfulRetUtils.getRetSuccess(sb.substring(0, sb.length() - 1));
        return json;
    }

    /**
     * 通过ckeditor上传图片
     * @param file
     * @param funcNumber
     * @return
     */
    @PostMapping("/ckeditor/uploadImage")
    @ResponseBody
    public RedirectView ckeditorUploadImage(@RequestParam("upload") MultipartFile file, @RequestParam("backUrl") String backUrl, @RequestParam("CKEditorFuncNum") String funcNumber, HttpServletResponse response) {
        String fileName = "", filePath = "", message = "";
        try {
            String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            // UUID生成文件名
            fileName = UUID.randomUUID().toString() + suffix;
            // 创建年月日的目录
            filePath = "CKEditor/Image/" + formatDate(new Date(), "yyyy") + "/" + formatDate(new Date(), "MM") + "/" + formatDate(new Date(), "dd") + "/";
            createDirectory(uploadFilePath + filePath);
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(uploadFilePath + filePath + fileName)));
            out.write(file.getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {
            message = "文件上传失败：获取上传文件失败";
            logger.error(e.getMessage(), e);
        }
        response.setHeader("X-Frame-Options", "SAMEORIGIN");
        return new RedirectView(backUrl + "?FileUrl=" + filePath + fileName + "&Message=" + message + "&CKEditorFuncNum=" + funcNumber);
    }

    /**
     * 通过ckeditor上传文件
     * @param file
     * @param funcNumber
     * @return
     */
    @PostMapping("/ckeditor/uploadFile")
    @ResponseBody
    public RedirectView ckeditorUploadFile(@RequestParam("upload") MultipartFile file, @RequestParam("backUrl") String backUrl, @RequestParam("CKEditorFuncNum") String funcNumber, HttpServletResponse response) {
        String fileName = "", filePath = "", message = "";
        try {
            String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            // UUID生成文件名
            fileName = UUID.randomUUID().toString() + suffix;
            // 创建年月日的目录
            filePath = "CKEditor/File/" + formatDate(new Date(), "yyyy") + "/" + formatDate(new Date(), "MM") + "/" + formatDate(new Date(), "dd") + "/";
            createDirectory(uploadFilePath + filePath);
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(uploadFilePath + filePath + fileName)));
            out.write(file.getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {
            message = "文件上传失败：获取上传文件失败";
            logger.error(e.getMessage(), e);
        }
        response.setHeader("X-Frame-Options", "SAMEORIGIN");
        return new RedirectView(backUrl + "?FileUrl=" + filePath + fileName + "&Message=" + message + "&CKEditorFuncNum=" + funcNumber);
    }

    /**
     * 格式化时间日期
     * @param date
     * @param pattern
     * @return
     */
    private String formatDate(Date date, Object... pattern) {
        String formatDate = null;
        if (pattern != null && pattern.length > 0) {
            formatDate = DateFormatUtils.format(date, pattern[0].toString());
        } else {
            formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
        }
        return formatDate;
    }

    /**
     * 创建目录
     * @param descDirName 目录名,包含路径
     * @return 如果创建成功，则返回true，否则返回false
     */
    private boolean createDirectory(String descDirName) {
        String descDirNames = descDirName;
        if (!descDirNames.endsWith(File.separator)) {
            descDirNames = descDirNames + File.separator;
        }
        File descDir = new File(descDirNames);
        if (descDir.exists()) {
            logger.debug("目录 " + descDirNames + " 已存在!");
            return false;
        }
        // 创建目录
        if (descDir.mkdirs()) {
            logger.debug("目录 " + descDirNames + " 创建成功!");
            return true;
        } else {
            logger.debug("目录 " + descDirNames + " 创建失败!");
            return false;
        }

    }
}
