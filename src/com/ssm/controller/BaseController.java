package com.ssm.controller;


import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.BreakClear;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.LineSpacingRule;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.VerticalAlign;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRelation;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDrawing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHyperlink;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ssm.model.Add;
import com.ssm.service.BaseService;
import com.ssm.util.MethodUtil;

@Controller
public class BaseController {
	
	private BaseService baseService;
	
	public BaseService getBaseService() {
		return baseService;
	}
	@Autowired
	public void setBaseService(BaseService baseService) {
		this.baseService = baseService;
	}
	
	@RequestMapping("getAll")
	public String getAddInfoAll(HttpServletRequest request){
		try {			
			List<Add> list = baseService.getAll();
			request.setAttribute("addLists", list);
			return "listAll";
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("InfoMessage", "信息载入失败！具体异常信息：" + e.getMessage());
			return "result";
		}
	}
	
	
		
	@SuppressWarnings("finally")
	@RequestMapping("addInfo")
	public void add(HttpServletRequest request,HttpServletResponse response){
		try {		
			HashMap paramMap = (HashMap) request.getParameterMap();
			Add add = new Add();
			add.setId(UUID.randomUUID().toString());
			add.setTname((String) ((String[]) paramMap.get("tname"))[0]);
			add.setTpwd((String) ((String[]) paramMap.get("password"))[0]);
			System.out.println(add.getId() + ":::::" + add.getTname() + ":::::" + add.getTpwd());
			String str = baseService.addInfo(add);
			System.out.println(str);
			request.setAttribute("InfoMessage", str);
			MethodUtil.toJsonMsg(response, 0, "成功");
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("InfoMessage", "添加信息失败！具体异常信息：" + e.getMessage());
		} finally {			
			//return "result";
		}
	}
	/*
	@SuppressWarnings("finally")
	@RequestMapping("del")
	public String del(String tid,HttpServletRequest request){
		try {			
			String str = baseService.delete(tid);
			request.setAttribute("InfoMessage", str);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("InfoMessage", "删除信息失败！具体异常信息：" + e.getMessage());
		} finally {			
			return "result";
		}
	}
	@RequestMapping("modify")
	public String modify(String tid,HttpServletRequest request){
		try {			
			Add add = baseService.findById(tid);
			request.setAttribute("add", add);
			return "modify";
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("InfoMessage", "信息载入失败！具体异常信息：" + e.getMessage());
			return "result";
		}
	}
	@SuppressWarnings("finally")
	@RequestMapping("update")
	public String update(Add add,HttpServletRequest request){
		try {			
			String str = baseService.update(add);
			request.setAttribute("InfoMessage", str);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("InfoMessage", "更新信息失败！具体异常信息：" + e.getMessage());
		} finally {			
			return "result";
		}
	}*/
public static void main(String[] args) {
	try {
		//htmlToWord2();
		//htmlToWord();
		//example();
		//htmlToWord3();
		inputStreamToWord3();
		String content="";
		//exportDoc("e:\\12.doc", content);
	} catch (Exception e) {
		e.printStackTrace();
	}
}

//----------------
//根据URL导出doc
public static void htmlToWord() throws Exception {  
	   URL url = new URL("http://zhidao.baidu.com/link?url=ySGmPCmUkPOaRC2AxaZfZHpAnE9-NXCiY93amBXJpAIgqeMpNydFaldQKrS6O_ZMIih9sOqx9nHKP-ckm4VkR_");  
	   InputStream is = url.openStream();  
	   OutputStream os = new FileOutputStream("e:\\000.doc");  
	   inputStreamToWords(is, os);  
	}  
	  
	/** 
	 * 把is写入到对应的word输出流os中 
	 * 不考虑异常的捕获，直接抛出 
	 * @param is 
	 * @param os 
	 * @throws IOException 
	 */  
	private static void inputStreamToWords(InputStream is, OutputStream os) throws IOException {  
	   POIFSFileSystem fs = new POIFSFileSystem();  
	   //对应于org.apache.poi.hdf.extractor.WordDocument  
	   fs.createDocument(is, "WordDocument");  
	   fs.writeFilesystem(os);  
	   os.close();  
	   is.close();  
	}  

//---------------------
	//根据html导出doc
public static void htmlToWord2() throws Exception {  
	   InputStream bodyIs = new FileInputStream("e://好好理财.htm");  
	   //InputStream cssIs = new FileInputStream("d:\\1.css");  
	   String body = getContent(bodyIs);  
	  // String css = this.getContent(cssIs);  
	   //拼一个标准的HTML格式文档  
	   String content = "<html><head><style>" /*+ css*/ + "</style></head><body>" + body + "</body></html>";  
	   InputStream is = new ByteArrayInputStream(content.getBytes("utf-8"));  
	   OutputStream os = new FileOutputStream("e:\\好好理财.doc");  
	   inputStreamToWord(is, os);  
	}  
	  
	/** 
	 * 把is写入到对应的word输出流os中 
	 * 不考虑异常的捕获，直接抛出 
	 * @param is 
	 * @param os 
	 * @throws IOException 
	 */  
	private static void inputStreamToWord(InputStream is, OutputStream os) throws IOException {  
	   POIFSFileSystem fs = new POIFSFileSystem();  
	   //对应于org.apache.poi.hdf.extractor.WordDocument  
	   fs.createDocument(is, "WordDocument");  
	   fs.writeFilesystem(os);  
	   os.close();  
	   is.close();  
	}  
	  
	/** 
	 * 把输入流里面的内容以UTF-8编码当文本取出。 
	 * 不考虑异常，直接抛出 
	 * @param ises 
	 * @return 
	 * @throws IOException 
	 */  
	private static String getContent(InputStream... ises) throws IOException {  
	   if (ises != null) {  
	      StringBuilder result = new StringBuilder();  
	      BufferedReader br;  
	      String line;  
	      for (InputStream is : ises) {  
	         br = new BufferedReader(new InputStreamReader(is, "UTF-8"));  
	         while ((line=br.readLine()) != null) {  
	             result.append(line);  
	         }  
	      }  
	      return result.toString();  
	   }  
	   return null;  
	}  
	
	
   
    
    
    //---------------------------------
	//doc-->doc导出
    private static void inputStreamToWord3() throws IOException {  
       InputStream is = new FileInputStream("e://12.doc");  
 	   OutputStream os = new FileOutputStream("e:\\666.doc");
 	   POIFSFileSystem fs = new POIFSFileSystem();  
 	   //对应于org.apache.poi.hdf.extractor.WordDocument  
 	   fs.createDocument(is, "WordDocument");  
 	   fs.writeFilesystem(os);  
 	   os.close();  
 	   is.close();  
 	}  
    
    
    
    //--------------------------
    //根据内容导出doc
    public static int exportDoc(String destFile,String fileContent){
    	fileContent ="wwwwwwwwwwwwwwwwwwwwwwwwwwww";
    	try {
    	ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileContent.getBytes("UTF-8"));
    	POIFSFileSystem fileSystem = new POIFSFileSystem();
    	DirectoryEntry directory = fileSystem.getRoot();
    	directory.createDocument("WordDocument", byteArrayInputStream);
    	 FileOutputStream fileOutputStream = new FileOutputStream(destFile);
    	fileSystem.writeFilesystem(fileOutputStream);
    	byteArrayInputStream.close();
    	fileOutputStream.close();
    	 return 1;
    	} catch (IOException e) {return 0;
    	 }
    	}
}
