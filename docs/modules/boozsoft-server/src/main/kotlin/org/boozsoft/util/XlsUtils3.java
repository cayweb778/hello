package org.boozsoft.util;

import cn.hutool.core.util.StrUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 读取Excel
 * hang 从第几行获取数据
 * @author zengwendong
 */
public class XlsUtils3 {
	private static Logger logger = LoggerFactory.getLogger(XlsUtils3.class);
	private static Workbook wb;
	private static Sheet sheet;
	private static Row row;

	public XlsUtils3(String filepath) {
		if (filepath == null) {
			return;
		}
		String ext = filepath.substring(filepath.lastIndexOf(".")).toLowerCase();
		try {
			InputStream is = new FileInputStream(filepath);
			if (".xls".equals(ext)) {
				wb = new HSSFWorkbook(is);
			} else if (".xlsx".equals(ext)) {
				wb = new XSSFWorkbook(is);
			} else {
				wb = null;
			}
		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException", e);
		} catch (IOException e) {
			logger.error("IOException", e);
		}
	}

	/**
	 * 读取Excel表格表头的内容
	 *
	 * @return String 表头内容的数组
	 * @author zengwendong
	 */
	public String[] readExcelTitle() throws Exception {
		if (wb == null) {
			throw new Exception("Workbook对象为空！");
		}
		sheet = wb.getSheetAt(0);
		row = sheet.getRow(0);
		// 标题总列数
		int b = row.getPhysicalNumberOfCells();
		int colNum=0;
		for (int i = 0; i < b; i++) {
			if (row.getCell(i)!=null) {
				colNum++;
			}
		}


		String[] title = new String[colNum];

		boolean num=false;
		for (int i = 0; i < colNum; i++) {
			title[i] = row.getCell(i).toString().replace("\n", "");
			if(title[i]==""){
				num=true;
			}
		}

//		if(num){
//			readExcelTitle();
//		}
		int a =0;
		for (int i = 0; i < title.length; i++) {
			if (title[i]!=null&&title[i]!="") {
				a=a+1;
			}
		}
		return readExcelTitles(a);
	}

	public int getRow(int hang) throws Exception {
		if (wb == null) {
			throw new Exception("Workbook对象为空！");
		}
		sheet = wb.getSheetAt(0);
		row = sheet.getRow(hang);
		// 标题总列数
		int b = row.getPhysicalNumberOfCells();
		int colNum=0;
		for (int i = 0; i < b; i++) {
			if (row.getCell(i)!=null) {
				colNum++;
			}
		}
		String[] title = new String[colNum];
		boolean num=false;
		for (int i = 0; i < colNum; i++) {
			title[i] = row.getCell(i).toString().replace("\n", "");
			if(title[i]==""){
				num=true;
			}
		}

		if(num){
			hang=hang+1;
		}
		return hang;
	}

	//重新循环表头，去除空表头
	public String[] readExcelTitles(int a) throws Exception {
		String[] title = new String[a];
		for (int i = 0; i < a; i++) {
			title[i] = row.getCell(i).toString().replace("\n", "");
		}
		return title;
	}

	/**
	 * 读取Excel数据内容
	 *
	 * @return Map 包含单元格数据内容的Map对象
	 * @author zengwendong
	 */
	public Map<Integer, Map<Integer, Object>> readExcelContent()
			throws Exception {
		if (wb == null) {
			throw new Exception("Workbook对象为空！");
		}
		Map<Integer, Map<Integer, Object>> content = new HashMap<Integer, Map<Integer, Object>>();

		sheet = wb.getSheetAt(0);
		// 得到总行数
		int rowNum = sheet.getLastRowNum();
		row = sheet.getRow(0);
		int colNum = row.getPhysicalNumberOfCells();
		// 正文内容应该从第二行开始,第一行为表头的标题
		for (int i = 1; i <= rowNum; i++) {
			row = sheet.getRow(i);
			if (row!=null) {//排除空行
				int j = 0;
				Map<Integer, Object> cellValue = new HashMap<Integer, Object>();
				while (j < colNum) {
					Object obj = getCellFormatValue(row.getCell(j));
					cellValue.put(j, obj);
					j++;
				}
				content.put(i, cellValue);
			}
		}
		return content;
	}

	/**
	 * 读取Excel数据内容
	 *
	 * @return Map 包含单元格数据内容的Map对象
	 * @author zengwendong
	 */
	public List<List<Object>> readExcelContent_() throws Exception {
		if (wb == null) {
			throw new Exception("Workbook对象为空！");
		}
		List<List<Object>> list = new ArrayList<List<Object>>();
		List<Object> li = null;
		sheet = wb.getSheetAt(0);
		// 得到总行数
		int rowNum = sheet.getLastRowNum();
		row = sheet.getRow(0);
		int colNum = row.getPhysicalNumberOfCells();
		// 第一行为表头的标题
		for (int i = 0; i <= rowNum; i++) {
			li = new ArrayList<Object>();
			row = sheet.getRow(i);
			if (isRowEmpty(row))continue; // 过滤空行
			int j = 0;
			while (j < colNum) {
				Object obj = getCellFormatValue(row.getCell(j));
				li.add(obj);
				j++;
			}
			list.add(li);
		}
		return list;
	}

	/**
	 *
	 * 根据Cell类型设置数据
	 *
	 * @param cell
	 * @return
	 * @author zengwendong
	 */
	private Object getCellFormatValue(Cell cell) {
		Object cellvalue = "";
		if (cell != null) {
			// 判断当前Cell的Type
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_NUMERIC:// 如果当前Cell的Type为NUMERIC
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				if (cell.toString().indexOf("月")>0) {
					double cellValue = cell.getNumericCellValue();
					Date date2 = DateUtil.getJavaDate(cellValue);
					cellvalue =sdf.format(date2); //new DecimalFormat("#").format(cellValue);//String.format("%.2f", cellValue)
				}else {
					//读取数据前设置单元格类型
		            cell.setCellType(CellType.STRING);
					cellvalue = cell.getStringCellValue();
				}
				break;
			case Cell.CELL_TYPE_FORMULA: {
				// 判断当前的cell是否为Date
				if (DateUtil.isCellDateFormatted(cell)) {
					// 如果是Date类型则，转化为Data格式
					// data格式是带时分秒的：2013-7-10 0:00:00
					// cellvalue = cell.getDateCellValue().toLocaleString();
					// data格式是不带带时分秒的：2013-7-10
					Date date = cell.getDateCellValue();
					cellvalue = date;
				} else {// 如果是纯数字

					// 取得当前Cell的数值
					cellvalue = String.valueOf(cell.getNumericCellValue());
				}
				break;
			}
			case Cell.CELL_TYPE_STRING:// 如果当前Cell的Type为STRING
				// 取得当前的Cell字符串
				cellvalue = cell.getRichStringCellValue().getString();
				break;
			default:// 默认的Cell值
				cellvalue = "";
			}
		} else {
			cellvalue = "";
		}
		return cellvalue;
	}

 public static List<Object[]> getExcelObj(String filepath, String[] dataTitles, int hang) {
		try {
			if (filepath == null) {
				return null;
			}
			String ext = filepath.substring(filepath.lastIndexOf("."));
			try {
				InputStream is = new FileInputStream(filepath);
				if (".xls".equals(ext)) {
					wb = new HSSFWorkbook(is);
				} else if (".xlsx".equals(ext)) {
					wb = new XSSFWorkbook(is);
				} else {
					wb = null;
				}
			} catch (FileNotFoundException e) {
				logger.error("FileNotFoundException", e);
			} catch (IOException e) {
				logger.error("IOException", e);
			}
			XlsUtils3 excelReader = new XlsUtils3(filepath);
			// 对读取Excel表格标题测试
			String[] title = excelReader.readExcelTitle();
			Map<Integer,String> m=new HashMap();
			for(int i=0;i<title.length;i++){
				if(title[i]==null&&title[i]==""){
					continue;
				}
				title[i]=title[i].trim() != "" ? title[i].trim() : "";
				m.put(i, title[i]);
			}

			//根据Map表头动态添加数据
			List<Object[]> objects=new ArrayList<>();
			/**
			 * 表头和数据字段的列是对应的,map中存1,为姓名 ,行数据中1就是每行的姓名
			 * */
			//第一层for遍历excel每行数据,
			//第二层for遍历每行数据中每个字段并且去重,
			//第三层for遍历每行数据中每个字段对应map表头
			List<List<Object>> list = excelReader.readExcelContent_();
			for (int t = hang; t < list.size(); t++) {// 循环行,去掉标题行
				List<Object> datas = (List<Object>) list.get(t);
				Object[] o=new Object[dataTitles.length];
				for(int i=0;i<datas.size();i++){
					if(datas.get(i)==null){
						continue;
					}
					String data=((String)datas.get(i)).trim() != "" ? ((String)datas.get(i)).trim() : "";
					for(int j=0;j<dataTitles.length;j++){
						//map[i]对应str[i]
						if(m.get(i)==null){
							continue;
						}
						if(m.get(i).equals(dataTitles[j])){
							o[j]=data;
							break;
						}
					}
				}
				objects.add(o);
			}
			return objects;
		}catch (Exception e){
			throw new RuntimeException(e);
		}
 }

	/**
	 * 校验当前行是否为空行
	 * @param row
	 * @return
	 */
	private  boolean isRowEmpty(Row row){
		         for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
			            Cell cell = row.getCell(c);
			            if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK)
				                 return false;
			         }
		         return true;
	}

	public static String changeExcelByList(String filepath, List<Object[]> list, int writeIndex) {
		FileInputStream is = null;
		try {
			if (filepath == null) {
				//return null;
			}
			String ext = filepath.substring(filepath.lastIndexOf("."));
			Workbook workbook = null;
			try {
				 is = new FileInputStream(filepath);
				if (".xls".equals(ext.toLowerCase())) {
					workbook = new HSSFWorkbook(is);
				} else if (".xlsx".equals(ext)) {
					workbook = new XSSFWorkbook(is);
				} else {
					workbook = null;
				}
				is.close();
			} catch (FileNotFoundException e) {
				logger.error("FileNotFoundException", e);
			} catch (IOException e) {
				logger.error("IOException", e);
			}
			Sheet sheetAt = workbook.getSheetAt(0);
			// 得到总行数
			int rowNum = sheet.getLastRowNum();
			// 第一行为表头的标题
			Font font = workbook.createFont();
			font.setColor(Font.COLOR_RED);
			int listLength = list.get(0).length;
			for (int i = 1; i <= rowNum; i++) {
			 	Row rowCell = sheetAt.getRow(i);
				// 修改
				String warnInfo = list.get(i-1)[listLength-1].toString();
				if (StrUtil.isNotBlank(warnInfo)){
					Cell cell = rowCell.getCell(writeIndex);
					cell.getCellStyle().setFont(font);
					cell.setCellValue(cell.getStringCellValue()+"("+warnInfo.substring(1)+")");
				}
			}
			FileOutputStream out = new FileOutputStream(filepath);
			workbook.write(out);
			workbook.close();
			out.close();
			return filepath;
		}catch (Exception e){
			throw new RuntimeException(e);
		}
	}

}
