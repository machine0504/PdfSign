package com.bitservice.controller;

import com.bitservice.*;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhubc
 * @date 2020/11/26  18:51
 */
@Controller
@CrossOrigin
public class TestController {
    public final static Logger log = LoggerFactory.getLogger(TestController.class);

    @RequestMapping("/toPage")
    public String toPage(ModelAndView modelAndView){
        return "test";
    }



    @RequestMapping("/download")
    @ResponseBody
    public Map test(String cont,String num,String dwid){
        Map map = new HashMap();
        try {
            //region 构建文件输出目录 outPath
            //总输出目录   Thread.currentThread().getContextClassLoader().getResource("").getPath() 获得resoure目录
            String strPath_out_all = Thread.currentThread().getContextClassLoader().getResource("")
                    .getPath() + File.separator + "uploads";
            //每次的输出目录
            String strPath_out = strPath_out_all + File.separator + dwid;
            File outPath = new File(strPath_out);
            if(!outPath.exists()){
                outPath.mkdirs();
            }

            //endregion

            //region 下载文件
            String gzFullPath = strPath_out + File.separator+"公章.png";
            String szFullPath = strPath_out + File.separator+"私章.png";
            Main.ImageSet2(cont,num,gzFullPath,szFullPath);
            map.put("type","success");
            map.put("data", Base64Util.encode(Base64Util.imageTobyte(gzFullPath)));
            /*response.setContentType("img/png");
            response.setCharacterEncoding("utf-8");*/
//            response.setHeader("Content-Disposition", "attachment;filename=" + "公章.png");
            /*DownLoadUtils.download(response,gzFullPath,"公章.png");*/
            /*downFile(strPath_out,"公章.png");*/
            //endregion

        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }



    @PostMapping("/upload")
    @ResponseBody
    public Map uploadFile(@RequestParam("file")MultipartFile file,String dwid){
        Map res = new HashMap();

        if(file.isEmpty()){
            res.put("type","error");
            res.put("msg","上传失败，文件不存在");
            return res;
        }

        String filename = file.getOriginalFilename();//获得上传的文件的文件名
        String filePath = Thread.currentThread().getContextClassLoader().getResource("")
                .getPath() + File.separator + "uploads" + File.separator + dwid;
        /*String filePath = new ClassPathResource("uploads").getPath();*/
        File filePath1 = new File(filePath);
        if(!filePath1.exists()){
            filePath1.mkdirs();
        }
        File uploadFile = new File(filePath+File.separator+filename);
        try {
            file.transferTo(uploadFile);
            log.info("上传成功");
            res.put("type","success");
            res.put("msg","上传成功！");
            return res;
        } catch (IOException e) {
            e.printStackTrace();
        }


        res.put("type","error");
        res.put("msg","上传失败！");
        return res;
    }
    @GetMapping("/result")
    public void result(HttpServletRequest request, HttpServletResponse response,String dwid,String password) throws IOException {
        String filePath = Thread.currentThread().getContextClassLoader().getResource("")
                .getPath() + File.separator + "uploads" + File.separator + dwid+ File.separator +"公章.png";
        String filePath1 = Thread.currentThread().getContextClassLoader().getResource("")
                .getPath() + File.separator + "uploads" + File.separator + dwid+ File.separator +"mdx.p12";
        String pdfPath = Test01.SignTest(filePath1,filePath,password,dwid);
        response.setCharacterEncoding("utf-8");
        response.setContentType("multipart/form-data");
        //response.setHeader("Content-Disposition", "xxx.pdf");
        response.setHeader("Content-Disposition", "attachment;filename=" + "test1.pdf");
        WriteUtils.writeBytes(pdfPath, response.getOutputStream());
        File file = new File(pdfPath);
        if (file.exists()) {
            DataOutputStream temps = new DataOutputStream(response.getOutputStream());
            DataInputStream in = new DataInputStream(new FileInputStream(pdfPath));
            byte[] b = new byte[2048];
            while ((in.read(b)) != -1) {
                temps.write(b);
                temps.flush();
            }
            in.close();
            temps.close();
        } else {
            log.error("文件不存在!");
        }
    }

}
