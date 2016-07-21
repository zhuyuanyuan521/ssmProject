package com.ssm.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import sun.misc.BASE64Decoder;

import com.alibaba.fastjson.JSON;

/**
 * 
 * @author zlh
 * @创建日期：2014年3月1日 上午10:24:08
 * 
 * @类说明：方法工具类，包含绝大多数公共方法
 */
public class MethodUtil {
	private static Log log = LogFactory.getLog(MethodUtil.class);

	/**
	 * 获取登录用户的IP地址
	 * 
	 * @param request
	 * @return String
	 * @throws Exception 
	 */
	public static String getIpAddr(HttpServletRequest request) throws Exception {
		org.springframework.core.io.Resource resource = new ClassPathResource("/black.properties");
    	Properties props = PropertiesLoaderUtils.loadProperties(resource);
    	String ip = request.getHeader("x-forwarded-for");
    	
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip.equals("0:0:0:0:0:0:0:1")) {
			ip = "本地";
		}
		
		String ipprops = new String(props.getProperty("ip")
				.getBytes("ISO8859-1"),"UTF-8");
		String ips[]=ipprops.split(",");
		
		for(int i=0;i<ips.length;i++){
			if(ips[i] != null && !ips[i].equals("")){
	    		if( ip.indexOf(ips[i]) > 0 || ip.indexOf(ips[i]) == 0){
	    			log.info("【黑名单】该ip已被列入黑名单：" + ip);
	    			throw new  Exception();
	    		}
	    	}
		}
		
		return ip;
	}

	// 返回网站地址
	public static String getPath(HttpServletRequest request) {
		String path = request.getScheme() + "://" + request.getServerName()
				+ ":" + request.getServerPort() + request.getContextPath()
				+ "/";
		return path;
	}

	/**
	 * MD5加密方法
	 * 
	 * @param str
	 * @param encoding
	 *            default UTF-8
	 * @param no_Lower_Upper
	 *            0,1,2 0：不区分大小写，1：小写，2：大写
	 * @return MD5Str
	 */
	public static String getMD5(String str, String encoding, int no_Lower_Upper) {
		if (null == encoding)
			encoding = "utf-8";
		StringBuffer sb = new StringBuffer();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] array = md.digest(str.getBytes(encoding));
			for (int i = 0; i < array.length; i++) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
						.toUpperCase().substring(1, 3));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (no_Lower_Upper == 0) {
			return sb.toString();
		}
		if (no_Lower_Upper == 1) {
			return sb.toString().toLowerCase();
		}
		if (no_Lower_Upper == 2) {
			return sb.toString().toUpperCase();
		}
		return null;
	}

	/**
	 * DES
	 * 
	 * @param arrBTmp
	 * @return
	 * @throws Exception
	 */
	private static Key getKey(byte[] arrBTmp) throws Exception {
		byte[] arrB = new byte[8];// 创建一个空的8位字节数组（默认值为0）
		for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) { // 将原始字节数组转换为8位
			arrB[i] = arrBTmp[i];
		}
		Key key = new javax.crypto.spec.SecretKeySpec(arrB, "DES");// 生成密钥
		return key;
	}

	/**
	 * DES
	 * 
	 * @param strIn
	 * @return
	 * @throws Exception
	 */
	private static byte[] hexStr2ByteArr(String strIn) throws Exception {
		byte[] arrB = strIn.getBytes("UTF-8");
		int iLen = arrB.length;

		// 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
		byte[] arrOut = new byte[iLen / 2];
		for (int i = 0; i < iLen; i = i + 2) {
			String strTmp = new String(arrB, i, 2);
			arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
		}
		return arrOut;
	}

	/**
	 * DES
	 * 
	 * @param arrB
	 * @return
	 * @throws Exception
	 */
	private static String byteArr2HexStr(byte[] arrB) throws Exception {
		int iLen = arrB.length;
		// 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
		StringBuffer sb = new StringBuffer(iLen * 2);
		for (int i = 0; i < iLen; i++) {
			int intTmp = arrB[i];
			// 把负数转换为正数
			while (intTmp < 0) {
				intTmp = intTmp + 256;
			}
			// 小于0F的数需要在前面补0
			if (intTmp < 16) {
				sb.append("0");
			}
			sb.append(Integer.toString(intTmp, 16));
		}
		return sb.toString();
	}

	/**
	 * DES方法 0为加密,1为解密
	 * 
	 * @param deskey
	 *            密钥
	 * @param str
	 *            待加密字符串
	 * @param type
	 *            0为加密,1为解密
	 * @return DES Str
	 */
	public static String getDES(String str, int type) {
		Cipher encryptCipher = null;
		Cipher decryptCipher = null;
		Security.addProvider(new com.sun.crypto.provider.SunJCE());
		try {
			if (isEmpty(CommonConstant.DES_KEY_DEFAULT)) {
				return null;
			}
			Key key = getKey(CommonConstant.DES_KEY_DEFAULT.getBytes("UTF-8"));
			encryptCipher = Cipher.getInstance("DES");
			encryptCipher.init(Cipher.ENCRYPT_MODE, key);
			decryptCipher = Cipher.getInstance("DES");
			decryptCipher.init(Cipher.DECRYPT_MODE, key);
//			if (type == 0) { // 0为加密
//				return byteArr2HexStr(encryptCipher.doFinal(str.getBytes("UTF-8")));
//			} else {
//				return new String(decryptCipher.doFinal(hexStr2ByteArr(str)));
//			}
			
			if (type == 0) { // 0为加密
				return byteArr2HexStr(encryptCipher.doFinal(str.getBytes("UTF-8")));
			} else {
				return new String(decryptCipher.doFinal(hexStr2ByteArr(str)),"UTF-8");
			}
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 
	 * @return
	 */
	public static MethodUtil getInstance() {
		return new MethodUtil();
	}

	/**
	 * 获取随机数
	 * 
	 * @param min
	 *            最小数
	 * @param max
	 *            最大数
	 * @return int
	 */
	public static int getRandom(int min, int max) {
		return (int) (Math.random() * (max - min) + min);
	}

	/**
	 * 获取随机数从1开始,格式10000000-99999999
	 * 
	 * @param number
	 *            随机数长度
	 * @return
	 */
	public static int getRandom(int number) {
		int max = 9;
		int min = 1;
		for (int i = 1; i < number; i++) {
			min = min * 10;
			max = max * 10 + 9;
		}
		return getRandom(min, max);
	}

	/**
	 * 获取日期方法
	 * 
	 * @param type
	 *            0:yyyy-MM-dd HH:mm:ss 1:yyyyMMddHHmmss 2:yyyyMMdd
	 * @param formatStr
	 *            日期格式
	 * @return String
	 */
	public static String getDate(int type, String formatStr) {
		Date date = new Date();
		SimpleDateFormat sdf = null;
		if (null != formatStr) {
			sdf = new SimpleDateFormat(formatStr);
		} else if (type == 0) {
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		} else if (type == 1) {
			sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		} else if (type == 2) {
			sdf = new SimpleDateFormat("yyyyMMdd");
		}
		String str = sdf.format(date);
		return str;
	}

	/**
	 * 时间差
	 * 
	 * @param current_time
	 *            当前时间
	 * @param compare_time
	 *            比较时间
	 * @return long
	 */
	public static long getDateCompare(String current_time, String compare_time) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long time = 0;
		try {
			Date c_tiem = sf.parse(current_time);
			Date com_time = sf.parse(compare_time);
			long l = c_tiem.getTime() - com_time.getTime() > 0 ? c_tiem
					.getTime() - com_time.getTime() : com_time.getTime()
					- c_tiem.getTime();
			time = l / 1000; // 算出超时秒数
		} catch (Exception e) {
			e.printStackTrace();
		}
		return time;
	}

	/**
	 * 处理时间的加减运算 60*60 为一个小时 60*60*24 为一天
	 * 
	 * @param startTime
	 * @param endTime
	 * @param type
	 *            0为加 1为减
	 * @return Date long
	 */
	public static long getDateAdd(String startTime, String endTime, int type) {
		long time = 0l;
		try {
			Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.parse(startTime);
			Date addLong = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.parse(endTime);
			switch (type) {
			case 0:
				time = (date.getTime() / 1000) + (addLong.getTime() / 1000);
				break;
			case 1:
				time = (date.getTime() / 1000) - (addLong.getTime() / 1000);
				break;
			default:
				time = (date.getTime() / 1000) + (addLong.getTime() / 1000);
				break;
			}
			date.setTime(time * 1000);
			time = date.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return time;
	}

	/**
	 * 一个月最大day
	 * 
	 * @param time
	 *            时间
	 * @return obj[0]=maxMonth; obj[1]=time
	 */
	public static Object[] getMaxMonth(String time) {
		Object[] obj = new Object[2];
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar a = Calendar.getInstance();
		a.setTime(date);
		a.set(Calendar.DATE, 1); // 把日期设置为当月第一天
		a.roll(Calendar.DATE, -1);// 日期回滚一天，也就是最后一天
		int maxMonth = a.get(Calendar.DATE);
		a.roll(Calendar.DATE, 1);
		time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(a.getTime());
		obj[0] = maxMonth;
		obj[1] = time;
		return obj;
	}

	/**
	 * 20位可用于UUID
	 * 
	 * @return String
	 */
	public static String getUid() {
		return new SimpleDateFormat("yyMMddHHmmss").format(new Date())
				+ getRandom(8);
	}

	/**
	 * 12位时间加上number位数
	 * 
	 * @param number
	 * @return Long
	 */
	public static Long getUid(int number) {
		return Long.parseLong(new SimpleDateFormat("yyMMddHHmmss")
				.format(new Date()) + getRandom(number));
	}

	/**
	 * 输出JSON
	 * 
	 * @param response
	 * @param type
	 *            0=成功 其他=失败
	 * @param msg
	 */
	public static void toJsonMsg(HttpServletResponse response, int type,
			String msg) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("state", type);
		if (type == 0) {
			map.put("success", true);
			if (msg == null) {
				map.put("msg", "成功");
			} else {
				map.put("msg", msg);
			}
		} else {
			map.put("success", false);
			if (msg == null) {
				map.put("msg", "失败");
			} else {
				map.put("msg", msg);
			}
		}
		toJsonPrint(response, JSON.toJSONString(map));
	}

	/**
	 * 输出JSON
	 * 
	 * @param response
	 * @param type
	 *            0=成功 其他=失败
	 * @param msg
	 */
	public static void toJsonMsg(HttpServletResponse response, String responseCode,
			String message) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("responseCode", responseCode);
		map.put("message", message);

		writer(response, JSON.toJSONString(map,true));
	}
	/**
	 * 打印JSON
	 * 
	 * @param response
	 * @param str
	 */
	public static void toJsonPrint(HttpServletResponse response, String str) {
		// 不需要设置 避免IE9 出现下载
		// response.setContentType("application/json");text/html;charset=UTF-8
		// response.setContentType("application/json");
		writer(response, str);
	}

	/**
	 * 打印
	 * 
	 * @param response
	 * @param str
	 */
	private static void writer(HttpServletResponse response, String str) {
		try {
			// 设置页面不缓存
			response.setHeader("Pragma", "No-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html");
			PrintWriter out = null;
			out = response.getWriter();
			out.print(str);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 在页面输出脚本
	 * 
	 * @param response
	 * @param str
	 */
	public void toSript(HttpServletResponse response, String str) {
		StringBuffer sb = new StringBuffer();
		sb.append("<script type=\"text/javascript\">");
		sb.append(str);
		sb.append("</script>");
		response.setContentType("text/html");
		writer(response, sb.toString());
	}

	/**
	 * 判断对象是否为空
	 * 
	 * @param obj
	 *            -参数对象
	 * @return boolean -true:表示对象为空;false:表示对象为非空
	 */
	public static boolean isEmpty(Object obj) {
		return obj == null || obj.toString().equalsIgnoreCase("null")
				|| obj.toString().length() == 0;
	}

	/**
	 * 将对应值转换为boolean
	 * 
	 * @param str
	 * @param flag
	 * @return
	 */
	public static boolean convertToBoolean(Object str, boolean flag) {
		if (isEmpty(str)) {
			return flag;
		}
		String strflag = convetToString(str);
		return Boolean.parseBoolean(strflag);
	}

	/**
	 * Object 转换为int类型
	 */
	public static Integer converToInt(Object str, Integer des) {
		if (isEmpty(str)) {
			return des;
		}
		return Integer.parseInt(str.toString());
	}

	public static int converToInt(Object str, int des) {
		if (isEmpty(str)) {
			return des;
		}
		return Integer.parseInt(str.toString());
	}

	/**
	 * 对象转换为Double
	 * 
	 * @param str
	 * @param des
	 * @return
	 */
	public static double converToDouble(Object str, double des) {
		if (isEmpty(str)) {
			return des;
		}
		return Double.parseDouble(str.toString());
	}

	public static Double converToDouble(Object str, Double des) {
		if (isEmpty(str)) {
			return des;
		}
		return Double.parseDouble(str.toString());
	}

	/**
	 * Object 转换为long类型
	 * 
	 * @param str
	 * @param des
	 * @return
	 */
	public static Long converToLong(Object str, Long des) {
		if (isEmpty(str)) {
			return des;
		}
		return Long.parseLong(str.toString());
	}

	public static long converToLong(Object str, long des) {
		if (isEmpty(str)) {
			return des;
		}
		return Long.parseLong(str.toString());
	}

	/**
	 * 转换为flaot类型
	 * 
	 * @param str
	 * @param des
	 * @return
	 */
	public static Float converToFloat(Object str, Float des) {
		if (isEmpty(str)) {
			return des;
		}
		return Float.parseFloat(str.toString());
	}

	public static float converToFloat(Object str, float des) {
		if (isEmpty(str)) {
			return des;
		}
		return Float.parseFloat(str.toString());
	}

	/**
	 * Object转换为String
	 * 
	 * @param obj
	 * @return
	 */
	public static String convetToString(Object obj) {
		if (isEmpty(obj)) {
			return "";
		}
		return obj.toString().trim();
	}

	/**
	 * 获得手续费 金额以分来算 返回金额，以元来算
	 * 
	 * @param money
	 * @param per
	 * @param max
	 * @return
	 */
	public static double getCostMoney(long money, int per, int max) {
		if (money == 0) {
			return 0.00;
		}
		if (per == 0) {
			return 0.00;
		}
		if (max == 0) {
			return 0.00;
		}
		if (per > max) {
			throw new ClassCastException("per cant greater max value");
		}
		double reval = money * per * 0.01 / max;
		return reval;
	}

	/**
	 * List集合中对象复制
	 * 
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void transFormEntity(List oldList, List newList, Class clas) {
		try {
			int size = 0;
			if (oldList != null && (size = oldList.size()) >= 1) {
				String className = clas.getName();
				Class myclass = Class.forName(className);
				for (int i = 0; i < size; i++) {
					Object obj = oldList.get(i);
					if (obj == null) {
						continue;
					}
					Object bean = myclass.newInstance();
					BeanUtils.copyProperties(obj, bean);
					newList.add(bean);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			log.error("MethodUtil transFormEntity have exception:" + e);
		}
	}

	/**
	 * 获得首页表的进度
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static String percent(double p1, double p2) {
		String str;
		double p3 = p1 * 100 / p2;
		DecimalFormat df1 = new DecimalFormat("###.##"); // ##.00%
		df1.setMinimumFractionDigits(2);
		str = df1.format(p3);
		return str;
	}

	/**
	 * 银行卡号码、身份证号码、手机号码、邮箱地址等信息处理
	 * 
	 * @param cardNo
	 * @param prefix
	 * @param suffix
	 * @param num 非0值为固定*为num长度，0值为动态
	 * @return
	 */
	public static String getSecurityNum(String cardNo, int prefix, int suffix, int num) {
		StringBuffer cardNoBuffer = new StringBuffer();
		int len = prefix + suffix;
		if (StringUtils.isNotBlank(cardNo) && cardNo.length() > len) {
			cardNoBuffer.append(cardNo.substring(0, prefix));
			if(num == 0){
				for (int i = 0; i < cardNo.length() - len; i++) {
					cardNoBuffer.append("*");
				}
			}else{
				for (int i = 0; i < num; i++) {
					cardNoBuffer.append("*");
				}
			}
			cardNoBuffer.append(cardNo.substring(cardNo.length() - suffix,
					cardNo.length()));
		}
		return cardNoBuffer.toString();
	}
	/**
	 * 银行卡号码、身份证号码、手机号码、邮箱地址等信息处理
	 * 
	 * @param cardNo
	 * @param prefix
	 * @param suffix
	 * @param num 非0值为固定*为num长度，0值为动态
	 * @str为替换符号
	 * @return
	 */
	public static String getSecurityNum(String cardNo, int prefix, int suffix, int num,String str) {
		StringBuffer cardNoBuffer = new StringBuffer();
		int len = prefix + suffix;
		if (StringUtils.isNotBlank(cardNo) && cardNo.length() > len) {
			cardNoBuffer.append(cardNo.substring(0, prefix));
			if(num == 0){
				for (int i = 0; i < cardNo.length() - len; i++) {
					cardNoBuffer.append(str);
				}
			}else{
				for (int i = 0; i < num; i++) {
					cardNoBuffer.append(str);
				}
			}
			cardNoBuffer.append(cardNo.substring(cardNo.length() - suffix,
					cardNo.length()));
		}
		return cardNoBuffer.toString();
	}
	/***************************************************************************
	 * MD5加码 生成32位md5码 用户提现md5加密方式
	 */
	private static String string2MD5(String inStr) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		char[] charArray = inStr.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++)
			byteArray[i] = (byte) charArray[i];
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();

	}

	/**
	 * 提现密码加密方式
	 * 
	 * @param str
	 * @return
	 */
	public static String paycodeToMD5(String str) {
		if (MethodUtil.isEmpty(str)) {
			return null;
		}
		String newStr = "haocai520" + str;
		String md5 = string2MD5(newStr);
		String newmd5 = string2MD5(md5);
		return newmd5;
	}

	/**
	 * excel 中rate()函数
	 * 
	 * @param a
	 *            现值
	 * @param b
	 *            年金
	 * @param c
	 *            期数
	 * @param cnt
	 *            循环次数
	 * @param ina
	 *            精确到小数点后10位
	 * @return
	 */
	public static double excelRate(double a, double b, double c, int cnt,
			int ina) {
		double rate = 1, x, jd = 0.1, side = 0.1, i = 1;
		do {
			x = a / b - (Math.pow(1 + rate, c) - 1)
					/ (Math.pow(rate + 1, c) * rate);
			if (x * side > 0) {
				side = -side;
				jd *= 10;
			}
			rate += side / jd;
		} while (i++ < cnt && Math.abs(x) >= 1 / Math.pow(10, ina));
		if (i > cnt)
			return Double.NaN;
		return rate;
	}
	
	
	/**
	 * 字符串转为 日期类型
	 * @param dateString  字符串
	 * @param dateFormat  字符串格式
	 * @return
	 */
	public static Date stringToDate(String dateString,String dateFormat){
		Date date = null;
		
		if (null == dateString || dateString.equals("")) {
			return new Date();
		}
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		   try {
		    date = format.parse(dateString);
		   } catch (ParseException e) {
		    e.printStackTrace();
		   }
		return date;
	}
	
	/**
	 * 得到系统当前时间戳
	 * @return
	 */
	public static Timestamp getCurrenTimestamp(){
		
		return new Timestamp(System.currentTimeMillis());
	}
	
//	 充值手续费
	public static Double getCostMoney(BigDecimal rechargeMoney,BigDecimal rate){
		BigDecimal rateFee = rechargeMoney.multiply(rate).multiply(new BigDecimal(0.01));
		return rateFee.doubleValue();
		
	}
	
	
	/**
	 * 普通的MD5加密算法
	 * @param inStr
	 * @return
	 */
	public static String MD5(String sourceStr) throws NoSuchAlgorithmException {
		String result = "";
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(sourceStr.getBytes());
		byte b[] = md.digest();
		int i;
		StringBuffer buf = new StringBuffer("");
		for (int offset = 0; offset < b.length; offset++) {
			i = b[offset];
			if (i < 0)
				i += 256;
			if (i < 16)
				buf.append("0");
			buf.append(Integer.toHexString(i));
		}
		result = buf.toString();
		return result;
	}
	
	
	/**
	 * 当前时间纳秒数 + 生成的 bits随机数
	 * @param bits
	 * @return
	 */
	public static String getRandomNum(int bits){
		//纳秒级时间量
		long s = System.nanoTime();
		StringBuffer sb = new StringBuffer("");
		sb.append(s);
		 for(int i = 0; i < bits; i ++){
	         sb.append((int)(Math.random()*10));
	      }
		return sb.toString();
		
	}
	
	
	
	
	
    /** 
     * 校验银行卡卡号 
     * @param cardId 
     * @return 
     */  
    public static boolean checkBankCard(String cardId) {  
          char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));  
          if(bit == 'N'){  
              return false;  
          }  
          return cardId.charAt(cardId.length() - 1) == bit;  
    }  
     
    /** 
     * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位 
     * @param nonCheckCodeCardId 
     * @return 
     */  
    public static char getBankCardCheckCode(String nonCheckCodeCardId){  
        if(nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0  
                || !nonCheckCodeCardId.matches("\\d+")) {  
         //如果传的不是数据返回N  
            return 'N';  
        }  
        char[] chs = nonCheckCodeCardId.trim().toCharArray();  
        int luhmSum = 0;  
        for(int i = chs.length - 1, j = 0; i >= 0; i--, j++) {  
            int k = chs[i] - '0';  
            if(j % 2 == 0) {  
                k *= 2;  
                k = k / 10 + k % 10;  
            }  
            luhmSum += k;             
        }  
        return (luhmSum % 10 == 0) ? '0' : (char)((10 - luhmSum % 10) + '0');  
    }  
	
    //base64加密解密
    public static String getBASE64(String s) {
		if (s == null) return null;
		return (new sun.misc.BASE64Encoder()).encode( s.getBytes() );
		}
    // 将 BASE64 编码的字符串 s 进行解码
	public static String getFromBASE64(String s) {
		if (s == null) return null;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			byte[] b = decoder.decodeBuffer(s);
			return new String(b);
		} catch (Exception e) {
			return null;
		}
	} 

	
	public static void main(String args[]){
		System.out.println(getBASE64("15112295427"));

	}
	
	
}
