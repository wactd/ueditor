package com.dong.ueditor.web;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UeditorController {

    @Value("${image.base.url}")
    private String imageBaseUrl;

    @Value("${image.base.path}")
    private String imageBasePath;

    /**
     * 获取配置文件
     * @param request
     * @return
     * @throws Exception
     */
    @ResponseBody
    @GetMapping(value = "/ueditor/config", params = "action=config",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String config(HttpServletRequest request) throws Exception {
        File file = ResourceUtils.getFile("classpath:" + "static/assets/UEditor/jsp/config.json");
        String jsonConfig = FileCopyUtils.copyToString(Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8));
        return jsonConfig;
    }

    @ResponseBody
    @PostMapping(value = "/ueditor/config", params = "action=uploadimage")
    public Map<String, Object> temp(HttpServletRequest request, @RequestParam(value = "upfile") MultipartFile upfile)
            throws IOException {
        Map<String, Object> resultMap = new HashMap<>();
        if (upfile.isEmpty()) {
            resultMap.put("state", "ERROR");
            return resultMap;
        }

        String fileName = upfile.getOriginalFilename();
        fileName = Instant.now().getNano() + "_" + fileName;
        File file = new File(imageBasePath + "/" + fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        upfile.transferTo(file);
        resultMap.put("state", "SUCCESS");
        resultMap.put("url", imageBaseUrl + "/" + fileName);
        return resultMap;
    }

}
