import java.io.*;
public class ConvertClass {
	public static void main(String args[]) throws IOException {
		
		String buffer = "";
		String filename = null;
		String mappingVariable=null;		//to store ActionMapping type variable
		String formName = null;
		char ch;
		int i;
		int tab_count = 0;
		byte b[];
		
		//ActionClasses is the directory containing all the action classes
		String myDirectoryPath  = "ActionClasses";
		File dir = new File(myDirectoryPath);
		File[] directoryListing = dir.listFiles();
		
		if(directoryListing != null) {
			for(File child : directoryListing) {
				
				FileInputStream fin = new FileInputStream(child);
				FileOutputStream fout = new FileOutputStream("spring/"+child);
		
				//FileInputStream fin = new FileInputStream("ActionClass.java");
				//FileOutputStream fout = new FileOutputStream("spring/ActionClass.java");
				
				
				while((i=fin.read()) != -1) {
					ch = (char)i;
					if((ch == ' ') || (ch == '\n') || (ch == '{') || (ch == '(') || (ch == ',') || (ch == ')')) {		//delimiters
						
						//if word is ActionForward replace with String
						if(buffer.equals("ActionForward")) {		
							replaceActionForward(fin,fout);
							buffer = "";
						}
						//if word is extends remove it and next word
						else if(buffer.equals("extends")) {	
							removeExtends(fin,fout);
							buffer = "";
						}
						//removing all the libraries and packages
						else if((buffer.equals("import")) || (buffer.equals("package"))) {
							while((i = fin.read()) != -1) {
								ch = (char)i;
								if(ch == ';') {
									break;
								}
							}
							buffer = "";
						}
						//if word is ActionMapping store its variable
						else if(buffer.equals("ActionMapping")) {
							mappingVariable = storeMappingVariable(fin,fout);			
							buffer = "";
						}
						//storing ActionForm name
						else if(buffer.equals("ActionForm")) {
							formName = getFormName(fin,fout);
				System.out.println("form name:"+formName);			
							buffer = "";
						}
						//dealing with ActionForm
						//returning only strings instead of map.findForward("String")						
						else if((mappingVariable != null) && (buffer.equals(mappingVariable+".findForward"))) {	
							replaceFindForward(fin,fout);
							buffer = "";
						}
						
						else{		
							buffer += ch;
							indentTab(fout,tab_count);
							tab_count = 0;				
							b = buffer.getBytes();
							fout.write(b);				
							if(ch == '\n'){
								
								System.out.println(buffer);
								buffer += '\n';
							}
							else{
								System.out.print(buffer);
							}
							
							buffer = "";
							
						}
						
					}
					else if(ch == '\t'){
						tab_count++;
					}
					else{	
						buffer += ch;
					}
				}
				fin.close();
				fout.close();
			}
		}
		
	}
	
	static void indentTab(FileOutputStream fout,int tab_count) throws IOException{
		byte b[];
		String buffer = "";
		
		buffer += '\t';
		b = buffer.getBytes();
		while(tab_count > 0){
			System.out.print('\t');
			fout.write(b);
			tab_count--;
		}
	}

	static void replaceActionForward(FileInputStream fin,FileOutputStream fout) throws IOException{
		byte b[];
		String buffer = "String ";
		
		b = buffer.getBytes();
		fout.write(b);
		System.out.print("String ");
	}
	
	static void removeExtends(FileInputStream fin,FileOutputStream fout) throws IOException {
		ignoreUntil(fin,fout,"Action");
	}
	
	static void ignoreUntil(FileInputStream fin,FileOutputStream fout,String str) throws IOException {		//ignores everything till String 'str'
		String buffer = "";
		char ch = ' ';
		int i;
		byte b[];
		
		while((i=fin.read())!=-1) {
			ch = (char)i;
			if((ch == ' ') || (ch == '\n') || (ch == '{') || (ch == '(') || (ch == '.')) {
				if(buffer.equals(str)) {
					break;
				}
				else{
					buffer = "";
				}
			}
			else if(ch == '\t'){
				//ignore
			}
			else {		
				buffer += ch;
			}
		}
		System.out.print(ch);
		buffer = "";
		buffer += ch;
		b = buffer.getBytes();
		fout.write(b);
	}
	
	static String storeMappingVariable(FileInputStream fin,FileOutputStream fout) throws IOException {
		String buffer = "";
		char ch;
		int i;
		byte b[];
		
		while((i=fin.read())!=-1) {
			ch = (char)i;
			if((ch == ' ') || (ch == '\n') || (ch == '{') || (ch == '(') || (ch == ',') || (ch == ')')) {
				//System.out.print("ActionMapping "+buffer+ch);
				break;
			}
			else {
				buffer += ch;
			}
		}
		return buffer;
		
	}
	
	static String getFormName(FileInputStream fin,FileOutputStream fout) throws IOException {
		String buffer = "";
		String temp_buffer = "";
		char ch;
		int i;
		byte b[];
		
		while((i=fin.read())!=-1) {
			ch = (char)i;
			if((ch == ' ') || (ch == '\n') || (ch == '{') || (ch == '(') || (ch == ',') || (ch == ')')) {
				System.out.print("ActionForm "+buffer+ch);
				temp_buffer = "ActionForm "+buffer+ch;
				b = temp_buffer.getBytes();
				fout.write(b);
				break;
			}
			else {
				buffer += ch;
			}
		}
		return buffer;
		
	}
	
	static void replaceFindForward(FileInputStream fin,FileOutputStream fout) throws IOException {
		String buffer = "";
		char ch;
		int i;
		int tab_count = 0;
		byte b[];
		
		while((i=fin.read())!=-1) {
			ch = (char)i;
			if(ch == '(') {
				//ignore
			}
			else if(ch == ')') {
				System.out.print(buffer);
				b = buffer.getBytes();
				fout.write(b);
				break;
			}
			else {
				buffer += ch;
			}
		}
	}
}
