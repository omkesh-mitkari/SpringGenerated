package project;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Enumeration;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ConvertActionForm extends HttpServlet{
	Object FormClassObj;
	String MethodName;
	String paraName,paraValue;
	Object temp;
	static Object obj;
	String tempname;
	
	public void putFormClassName(Object FormClassObj) {
		String className ;
		
//		className = String.valueOf(FormClassObj);
		this.FormClassObj = FormClassObj;
		System.out.println("form class:"+String.valueOf(FormClassObj));
	}
	
public Object formBean(HttpServletRequest req) {
		Enumeration names = req.getParameterNames();
		Class fieldType;
		
		while(names.hasMoreElements()) {
			temp = names.nextElement();
			paraName = (String)temp;
			paraValue = req.getParameter(paraName);
			
	System.out.println("current attribute:"+paraName);		
			
			obj = FormClassObj;
			Class<?> tempclass = obj.getClass();
			
			for(Field field : tempclass.getDeclaredFields()) {	
				fieldType = field.getType();
			    tempname = field.toString();
				tempname = extractFieldName(tempname);	  
		System.out.println("fieldname,paraname:"+tempname+","+paraName);		
			       if(tempname.equals(paraName)) {
			    	 //convert parameter name according to the function name in form class
						paraName = convertString(paraName);			
						MethodName = "set"+paraName;
			System.out.println("method name:"+MethodName);			
						Class<? extends Object> c = FormClassObj.getClass();
						try {
							Method m = c.getDeclaredMethod(MethodName,fieldType);
							m.invoke(obj,paraValue);
						} catch (NoSuchMethodException | SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							
						}
			       }
			   }		
		}
		return obj;
}


private static String extractFieldName(String field) {
	int len,i,start=0,len_temp;
	char arr[],temp_arr[];
	
	len = field.length();
	arr = new char[len];
	
	for(i = 0 ; i < len ; i++) {
		arr[i] = field.charAt(i);
		if(arr[i] == '.') {
			start = i+1;
		}
	}
	len_temp = len-start;
	temp_arr = new char[len_temp];
		
	for(i = 0 ; i < len_temp ; i++,start++) {
		temp_arr[i] = arr[start];
	}
	
	return String.valueOf(temp_arr);
}

private static String convertString(String paraName) {
	int difference = 0;
	int len,i;
		
		//convert string to char array
		len = paraName.length();
		char arr[] = new char[len];
		for(i = 0 ; i < len ; i++) {
			arr[i] = paraName.charAt(i);
		}		
		//converting the first letter from lowercase to uppercases
		//calculate the difference of ascii of first letter from 'a'
		difference = (int)arr[0] - (int)'a';
		//add the difference to ascii value of 'A' 
		arr[0] = (char)((int)'A'+difference);
		//convert char array back to string
		paraName = String.valueOf(arr);
		return paraName;	
	}
}