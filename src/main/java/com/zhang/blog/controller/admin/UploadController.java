package com.zhang.blog.controller.admin;

import com.zhang.blog.Types;
import com.zhang.blog.api.QiNiuCloudService;
import com.zhang.blog.config.Constants;
import com.zhang.blog.entity.AdminUser;
import com.zhang.blog.entity.Attach;
import com.zhang.blog.service.AttAchService;
import com.zhang.blog.util.MyBlogUtils;
import com.zhang.blog.util.Result;
import com.zhang.blog.util.ResultGenerator;
import com.zhang.blog.util.TaleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @author zxs
 */
@Controller
@RequestMapping("/admin")
public class UploadController {

    @Autowired
    private AttAchService attAchService;

    @PostMapping({"/upload/file2"})
    @ResponseBody
    public Result upload(HttpServletRequest httpServletRequest, @RequestParam("file") MultipartFile file) throws URISyntaxException {
        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        //生成文件名称通用方法
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Random r = new Random();
        StringBuilder tempName = new StringBuilder();
        tempName.append(sdf.format(new Date())).append(r.nextInt(100)).append(suffixName);
        String newFileName = tempName.toString();
        File fileDirectory = new File(Constants.FILE_UPLOAD_DIC);
        //创建文件
        File destFile = new File(Constants.FILE_UPLOAD_DIC + newFileName);
        try {
            if (!fileDirectory.exists()) {
                if (!fileDirectory.mkdir()) {
                    throw new IOException("文件夹创建失败,路径为：" + fileDirectory);
                }
            }
            file.transferTo(destFile);
            Result resultSuccess = ResultGenerator.genSuccessResult();
            resultSuccess.setData(MyBlogUtils.getHost(new URI(httpServletRequest.getRequestURL() + "")) + "/upload/" + newFileName);
            return resultSuccess;
        } catch (IOException e) {
            e.printStackTrace();
            return ResultGenerator.genFailResult("文件上传失败");
        }
    }

    @PostMapping({"/upload/file"})
    @ResponseBody
    public Result filesUploadToCloud(HttpServletRequest request,  @RequestParam("file") MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            //生成文件名称通用方法
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            Random r = new Random();
            StringBuilder tempName = new StringBuilder();
            tempName.append(sdf.format(new Date())).append(r.nextInt(100)).append(suffixName);
            String newFileName = tempName.toString();

            //String fileName = TaleUtils.getFileKey(file.getOriginalFilename().replaceFirst("/", ""));

            QiNiuCloudService.upload(file, newFileName);
            Attach attAchDomain = new Attach();
            HttpSession session = request.getSession();
            Integer adminUserId = (Integer) session.getAttribute("loginUserId");
            attAchDomain.setAuthorId(adminUserId);
            attAchDomain.setFtype(TaleUtils.isImage(file.getInputStream()) ? Types.IMAGE.getType() : Types.FILE.getType());
            attAchDomain.setFname(newFileName);
            attAchDomain.setFkey(QiNiuCloudService.QINIU_UPLOAD_SITE + newFileName);
            attAchDomain.setCreateTime(new Date());
            attAchService.addAttAch(attAchDomain);
            Result resultSuccess = ResultGenerator.genSuccessResult();
            resultSuccess.setData(attAchDomain.getFkey());
            return resultSuccess;

        } catch (IOException e) {
            e.printStackTrace();
            return ResultGenerator.genFailResult("文件上传失败");
        }

    }
}
