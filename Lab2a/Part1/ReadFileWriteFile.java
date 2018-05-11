import java.io.*; 
import java.util.*; 

/* 
 *  The program reads an input file "data.txt"  that has entries of the form 
 *  0	0.000000	I	536	98.190	92.170	92.170
 *  4	133.333330	P	152	98.190	92.170	92.170
 * 	1	33.333330	B	136	98.190	92.170	92.170
 *
 * The file is read line-by-line, values are parsed and assigned to variables,
 * values are  displayed, and then written to a file with name "output.txt"  
 */

class ReadFileWriteFile {  
	public static void main (String[] args) { 
		
		
		BufferedReader bis = null; 
		String currentLine = null; 
		PrintStream pout = null;

		long IFrameSize = 0, PFrameSize = 0, BFrameSize = 0;
		long IFrameCount = 0, PFrameCount = 0, BFrameCount = 0;
		
		try {  
			
			/*
			 * Open input file as a BufferedReader
			 */ 
			File fin = new File("movietrace.data.txt");
			FileReader fis = new FileReader(fin);  
			bis = new BufferedReader(fis);  
			
			/*
			 * Open file for output 
			 */
			FileOutputStream fout =  new FileOutputStream("output.txt");
			pout = new PrintStream (fout);
			/*
			 *  Read file line-by-line until the end of the file 
			 */
			while ( (currentLine = bis.readLine()) != null) { 
				
				/*
				 *  Parse line and break up into elements 
				 */
				StringTokenizer st = new StringTokenizer(currentLine); 
				String col1 = st.nextToken(); 
				String col2 = st.nextToken(); 
				String col3  = st.nextToken(); 
				String col4 = st.nextToken(); 
				
				/*
				 *  Convert each element to desired data type 
				 */
				int SeqNo 	= Integer.parseInt(col1);
				float Ftime 	= Float.parseFloat(col2);
				String Ftype 	= col3;
				int Fsize 	= Integer.parseInt(col4);


				switch (col3){
                    case "I":
                        IFrameSize += Fsize;
                        IFrameCount++;
                        break;
                    case "P":
                        PFrameSize += Fsize;
                        PFrameCount++;
                        break;
                    case "B":
                        BFrameSize += Fsize;
                        BFrameCount++;
                        break;
                    default:
                        break;
                }
				
				/*
				 *  Display content of file 
				 */
				System.out.println("SeqNo:  " + SeqNo); 
				System.out.println("Frame time:   " + Ftime); 
				System.out.println("Frame type:        " + Ftype); 
				System.out.println("Frame size:       " + Fsize + "\n"); 
				
				
				/*
				 *  Write line to output file 
				 */
				pout.println(SeqNo+ "\t"+  Ftime + "\t" + Ftype + "\t" + Fsize); 
				
			}
			//pout.println()
            System.out.println("I Frame Average Size: " + IFrameSize/IFrameCount);
            System.out.println("P Frame Average Size: " + PFrameSize/PFrameCount);
            System.out.println("B Frame Average Size: " + BFrameSize/BFrameCount);
		} catch (IOException e) {  
			// catch io errors from FileInputStream or readLine()  
			System.out.println("IOException: " + e.getMessage());  
		} finally {  
			// Close files   
			if (bis != null) { 
				try { 
					bis.close(); 
					pout.close();
				} catch (IOException e) { 
					System.out.println("IOException: " +  e.getMessage());  
				} 
			} 
		} 
	}  
}

