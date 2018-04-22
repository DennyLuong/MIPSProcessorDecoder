package comparch;

import java.io.*;
import java.io.IOException;

public class Source {
	static int rsIndex = 6;
	static int rtIndex = 11;
	static int immIndex = 16;

	static int rdIndex = 16;
	static int shIndex = 21;
	static int functIndex = 26;
	static int rs;
	static int rt;
	static int rd;
	static int sh;
	static long imm;

	static int[] regs = new int[32];
	static int temp = 0;
	static int regTemp = 0;

	static int[] memArr = new int[999];
	static String fileFlag = "";
	static String outputLine = null;

	static int regParse;

	static Converter iNumConv = new Converter();

	static int pc = 0;
	static int cCount = 1;
	static int brCounter = 0;

	static StringBuffer outputBuffer = new StringBuffer();

	public static void main(String[] args) {
		try {
			File inputFile = new File("input.txt");
			FileReader fileReader = new FileReader(inputFile);
			BufferedReader buffReader = new BufferedReader(fileReader);
			StringBuffer strBuffer = new StringBuffer();
			String line;

			while ((line = buffReader.readLine()) != null) {
				strBuffer.append(line);
				//				System.out.println(line);
				//				System.out.println("LINE");
				outputLine = instDecoder(line);
				System.out.println("outputLine: " + outputLine);
				strBuffer.append("\n");
//				outputBuffer.append(outputLine);
//				outputBuffer.append("\n");
			}
			fileReader.close();
			System.out.println("Contents of file:");
			System.out.println(strBuffer.toString());
			System.out.println("done");
			BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
			System.out.println(outputBuffer);

		    writer.write(outputBuffer.toString());
		    writer.write("REGISTERS\n");
		    for(int i = 0 ; i < regs.length ; i++)
		    {	
		    	if(regs[i] != 0) {
		    		System.out.println("R" + i + " " + regs[i]);
		    		writer.write("R" + i + " " + regs[i] + "\n");
		    	}
		    }
		    writer.write("MEMORY\n");
		    for(int i = 0 ; i < memArr.length ; i++)
		    {	
		    	if(memArr[i] != 0) {
		    		System.out.println("MEM: " + i + " " + memArr[i]);
		    		writer.write(i + " " + memArr[i] + "\n");
		    	}
		    }
		    writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String instDecoder(String bInst) {
		String decodedInst = bInst;
		String opcode, funct, imm;
		if(brCounter == 0) {
			if (bInst == "\n") {
				decodedInst = "New Line";
			}
			else{
//				System.out.println("Line Decoded");
				if(bInst.contains("REGISTERS")) {
	//				System.out.println("In REGISTERS");
					fileFlag = "REGISTERS";
				}
				else if(bInst.contains("MEMORY")) {
	//				System.out.println("In MEMORY");
					fileFlag = "MEMORY";
				}
				else if(bInst.contains("CODE")) {
	//				System.out.println("In CODE");
					fileFlag = "CODE";
				}
				else {
	//				System.out.println("ERROR");
				}
				switch (fileFlag) {
					case "REGISTERS":
						if(bInst.length() > 1) {
							if(!(bInst.contains("REGISTERS"))) {
								String tempRegStr = bInst.substring(1,3);
								tempRegStr = tempRegStr.replaceAll("\\s+","");
								regParse = Integer.valueOf(tempRegStr);
	//							System.out.println("tRStr: " + tempRegStr + "|");
	//							System.out.println("regParse: " + regParse);
								tempRegStr = bInst.substring(3);
								int regContents = Integer.valueOf(tempRegStr);
	//							System.out.println("regContents: " + regContents);

								regs[regParse] = regContents;
								System.out.println("R" + regParse + ": " + regs[regParse]);
							}
						}
						break;
					case "MEMORY":
						if(bInst.length() > 1) {
							if(!(bInst.contains("MEMORY"))) {
								int tempLoc = 0;
								int tempCont = 0;
								String delim = "\\s+";

								String[] tempRegStr = bInst.split(delim);
								for (int i = 0 ; i < tempRegStr.length  ; i++) {
	//								System.out.println("it:" + tempRegStr[i]);
									if(i == 0) {
										tempLoc = Integer.parseInt(tempRegStr[i]);
									}
									else if (i == 1) {
										tempCont = Integer.parseInt(tempRegStr[i]);
									}
								}
								memArr[tempLoc] = tempCont;
	//							System.out.println("tempLoc: " + tempLoc + " tempCont: " + tempCont);
								System.out.println("memArr" + tempLoc + ": " + memArr[tempLoc]);
							}
						}
						break;
					case "CODE":
						if(bInst.length() > 6) {
							pc++;
							opcode = bInst.substring(0, 6);
	//						System.out.println("opcode: " + opcode);
							if (opcode.contains("000000")) {
								funct = bInst.substring(26, 32);
								switch (funct) {
									case "100000":
										System.out.println(outputBuffer.append(add(bInst)));
										break;
									case "101010":
										System.out.println(outputBuffer.append(slt(bInst)));
										break;
									case "100010":
										System.out.println(outputBuffer.append(sub(bInst)));
										break;
									default:
										System.out.println("default Rtype");
										break;
								}
							}
							else {
								 switch (opcode) {
								 	case "001000":
										System.out.println(outputBuffer.append(addi(bInst)));
								 		break;
									case "000100":
										System.out.println(outputBuffer.append(beq(bInst)));
								 		break;
									case "000101":
										System.out.println(outputBuffer.append(bne(bInst)));
								 		break;
									case "100011":
										System.out.println(outputBuffer.append(lw(bInst)));
								 		break;
									case "101011":
										System.out.println(outputBuffer.append(sw(bInst)));
								 		break;
									default:
										System.out.println("default Itype");
										break;
								 }
							}
						}
						break;
					default:
						System.out.println("DEFAULT sw");
					 break;
				}
			}
		}
		else {
			brCounter--;
			if(brCounter >= 0) {
				brCounter = 0;
			}
		}
		return decodedInst;
	}

		public static String addi(String uInst) {
			String tempStr = "addi";
	      for (int i = 0 ; i < 2 ; i++) {
	        String regStr = uInst.substring(rsIndex + (i*5), rtIndex + (i*5));
	        System.out.println("i: " + i + " regStr: "  + regStr);
	        temp = Integer.parseInt(regStr);
	        regTemp = (int) iNumConv.binToDec(temp);
//	        regs[regTemp] = regTemp;
//	        System.out.println("currReg: " + regs[regTemp]);
	        if (i == 0)  {
	         rs = regTemp;
	        }
	        else if (i == 1) {
	         rt = regTemp;
	        }
	      }
	      
	      String iStr = uInst.substring(immIndex, 32);
	      imm = Long.parseLong(iStr);
	      imm = iNumConv.binToDec(imm);
	      System.out.println("rs: " + rs + " rt: " + rt + " imm: " + imm);

	      regs[rt] = (int) (regs[rs] + imm);
	      System.out.println("reg" +  rt + " :" + regs[rt]);
	      
	      String [] cyInst = {"IF", "ID", "EX", "WB"};
			StringBuffer lwBuffer = new StringBuffer();
			int inCount = 4;
			
			for (int i = 0 ; i < inCount ; i++) {
		    	  lwBuffer.append("C#" + (cCount+i) + " I" + pc + "-" + cyInst[i]);
		    	  if(i <= inCount - 1) {
		    		  lwBuffer.append("\n");
		    	  }
		      }
		      cCount += inCount; //FDEW
		      System.out.println(tempStr = lwBuffer.toString());
			return tempStr;
		}
		
		public static String beq(String uInst) {
			String tempStr = "beq";
			for (int i = 0 ; i < 2 ; i++) {
		        String regStr = uInst.substring(rsIndex + (i*5), rtIndex + (i*5));
//		        System.out.println("i: " + i + " regStr: "  + regStr);
		        temp = Integer.parseInt(regStr);
		        regTemp = (int) iNumConv.binToDec(temp);
//		        regs[regTemp] = regTemp;
//		        System.out.println("currReg: " + regs[regTemp]);
		        if (i == 0)  {
		         rs = regTemp;
		        }
		        else if (i == 1) {
		         rt = regTemp;
		        }
		      }
			String iStr = uInst.substring(immIndex, 32);
		      imm = Long.parseLong(iStr);
		      imm = iNumConv.binToDec(imm);
		      System.out.println("rs: " + rs + " rt: " + rt + " imm: " + imm);

			if(rs == rt) {
				brCounter = (int) imm;
			}
			
			String [] cyInst = {"IF", "ID", "EX"};
			StringBuffer lwBuffer = new StringBuffer();
			int inCount = 3;
			
			for (int i = 0 ; i < inCount ; i++) {
		    	  lwBuffer.append("C#" + (cCount+i) + " I" + pc + "-" + cyInst[i]);
		    	  if(i <= inCount - 1) {
		    		  lwBuffer.append("\n");
		    	  }
		      }
		      cCount += inCount; //FDE
		      System.out.println(tempStr = lwBuffer.toString());
		      
			return tempStr;
		}
		
		public static String bne(String uInst) {
			String tempStr = "bne";
			for (int i = 0 ; i < 2 ; i++) {
		        String regStr = uInst.substring(rsIndex + (i*5), rtIndex + (i*5));
//		        System.out.println("i: " + i + " regStr: "  + regStr);
		        temp = Integer.parseInt(regStr);
		        regTemp = (int) iNumConv.binToDec(temp);
//		        regs[regTemp] = regTemp;
//		        System.out.println("currReg: " + regs[regTemp]);
		        if (i == 0)  {
		         rs = regTemp;
		        }
		        else if (i == 1) {
		         rt = regTemp;
		        }
		      }
			String iStr = uInst.substring(immIndex, 32);
		      imm = Long.parseLong(iStr);
		      imm = iNumConv.binToDec(imm);
		      System.out.println("rs: " + rs + " rt: " + rt + " imm: " + imm);

			if(rs != rt) {
				brCounter = (int) imm;
			}
			
			String [] cyInst = {"IF", "ID", "EX"};
			StringBuffer lwBuffer = new StringBuffer();
			int inCount = 3;
			
			for (int i = 0 ; i < inCount ; i++) {
		    	  lwBuffer.append("C#" + (cCount+i) + " I" + pc + "-" + cyInst[i]);
		    	  if(i <= inCount - 1) {
		    		  lwBuffer.append("\n");
		    	  }
		      }
		      cCount += inCount; //FDE
		      System.out.println(tempStr = lwBuffer.toString());
		    
			return tempStr;
		}
		
		public static String lw(String uInst) {
		String tempStr = "lw";
		String [] cyInst = {"IF", "ID", "EX", "MEM", "WB"};
		StringBuffer lwBuffer = new StringBuffer();
		int inCount = 5;
	      for (int i = 0 ; i < 2 ; i++) {
	        String regStr = uInst.substring(rsIndex + (i*5), rtIndex + (i*5));
//	        System.out.println("i: " + i + " regStr: "  + regStr);
	        temp = Integer.parseInt(regStr);
	        regTemp = (int) iNumConv.binToDec(temp);
//	        regs[regTemp] = regTemp;
//	        System.out.println("currReg: " + regs[regTemp]);
	        if (i == 0)  {
	         rs = regTemp;
	        }
	        else if (i == 1) {
	         rt = regTemp;
	        }
	      }
	      String iStr = uInst.substring(immIndex, 32);
	      imm = Long.parseLong(iStr);
	      imm = iNumConv.binToDec(imm);
	      System.out.println("rs: " + rs + " rt: " + rt + " imm: " + imm);

				regs[rt] = (int) (memArr[regs[rs]+(int)imm]);
	      System.out.println("reg" +  rs + ": " + regs[rs]);
	      System.out.println("reg" +  rt + ": " + regs[rt]);
	     
	      for (int i = 0 ; i < inCount ; i++) {
	    	  lwBuffer.append("C#" + (cCount+i) + " I" + pc + "-" + cyInst[i]);
	    	  if(i <= inCount - 1) {
	    		  lwBuffer.append("\n");
	    	  }
	      }
	      cCount += inCount; //FDEMW
	      System.out.println((tempStr = lwBuffer.toString()));
	      return tempStr;
		}
		
		public static String sw(String uInst) {
			String tempStr = "sw";
			for (int i = 0 ; i < 2 ; i++) {
		        String regStr = uInst.substring(rsIndex + (i*5), rtIndex + (i*5));
//		        System.out.println("i: " + i + " regStr: "  + regStr);
		        temp = Integer.parseInt(regStr);
		        regTemp = (int) iNumConv.binToDec(temp);
//		        regs[regTemp] = regTemp;
//		        System.out.println("currReg: " + regs[regTemp]);
		        if (i == 0)  {
		         rs = regTemp;
		        }
		        else if (i == 1) {
		         rt = regTemp;
		        }
		      }
		      String iStr = uInst.substring(immIndex, 32);
		      imm = Long.parseLong(iStr);
		      imm = iNumConv.binToDec(imm);
		      System.out.println("rs: " + rs + " rt: " + rt + " imm: " + imm);

		      memArr[regs[rs] + (int)imm] = regs[rt];
		      System.out.println("reg" +  rs + ": " + regs[rs]);
		      System.out.println("reg" +  rt + ": " + regs[rt]);
		      System.out.println("memArr" +  regs[rs] + ": " + memArr[regs[rs]]);
		      String [] cyInst = {"IF", "ID", "EX", "MEM"};
				StringBuffer lwBuffer = new StringBuffer();
				int inCount = 4;
				
				for (int i = 0 ; i < inCount ; i++) {
			    	  lwBuffer.append("C#" + (cCount+i) + " I" + pc + "-" + cyInst[i]);
			    	  if(i <= inCount - 1) {
			    		  lwBuffer.append("\n");
			    	  }
			      }
			      cCount += inCount; //FDE
			      System.out.println(tempStr = lwBuffer.toString());
			return tempStr;
		}

		public static String add(String uInst) {
			String tempStr = "add";
			for (int i = 0 ; i < 4 ; i++) {
		        String regStr = uInst.substring(rsIndex + (i*5), rtIndex + (i*5));
//		        System.out.println("i: " + i + " regStr: "  + regStr);
		        temp = Integer.parseInt(regStr);
		        regTemp = (int) iNumConv.binToDec(temp);
//		        regs[regTemp] = regTemp;
//		        System.out.println("currReg: " + regs[regTemp]);
		        if (i == 0)  {
		         rs = regTemp;
		        }
		        else if (i == 1) {
		         rt = regTemp;
		        }
		        else if (i == 2) {
		         rd = regTemp;
		        }
		        else if (i == 3) {
		         sh = regTemp;
		        }
		      }
			regs[rd] = regs[rs] + regs[rt];
			System.out.println("rs: " + rs + " rt: " + rt);
			System.out.println("rd: " + rd + " sh: " + sh);
			System.out.println("R" + rd + ": " + regs[rd]);
			
			
			String [] cyInst = {"IF", "ID", "EX", "WB"};
			StringBuffer lwBuffer = new StringBuffer();
			int inCount = 4;
			
			for (int i = 0 ; i < inCount ; i++) {
		    	  lwBuffer.append("C#" + (cCount+i) + " I" + pc + "-" + cyInst[i]);
		    	  if(i <= inCount - 1) {
		    		  lwBuffer.append("\n");
		    	  }
		      }
		      cCount += inCount; //FDEW
		      System.out.println(tempStr = lwBuffer.toString());
		      
			return tempStr;
		}
		public static String slt(String uInst) {
			String tempStr = "slt";
			for (int i = 0 ; i < 4 ; i++) {
		        String regStr = uInst.substring(rsIndex + (i*5), rtIndex + (i*5));
//		        System.out.println("i: " + i + " regStr: "  + regStr);
		        temp = Integer.parseInt(regStr);
		        regTemp = (int) iNumConv.binToDec(temp);
//		        regs[regTemp] = regTemp;
//		        System.out.println("currReg: " + regs[regTemp]);
		        if (i == 0)  {
		         rs = regTemp;
		        }
		        else if (i == 1) {
		         rt = regTemp;
		        }
		        else if (i == 2) {
		         rd = regTemp;
		        }
		        else if (i == 3) {
		         sh = regTemp;
		        }
		      }
			if(regs[rs] < regs[rt])
			{
				regs[rd] = 1;
			}
			System.out.println("rs: " + rs + " rt: " + rt);
			System.out.println("rd: " + rd + " sh: " + sh);

			System.out.println("RD| R" + rd + ": " + regs[rd]);
			System.out.println("RS| R" + rs + ": " + regs[rs]);
			System.out.println("RT| R" + rt + ": " + regs[rt]);
			
			String [] cyInst = {"IF", "ID", "EX", "WB"};
			StringBuffer lwBuffer = new StringBuffer();
			int inCount = 4;
			
			for (int i = 0 ; i < inCount ; i++) {
		    	  lwBuffer.append("C#" + (cCount+i) + " I" + pc + "-" + cyInst[i]);
		    	  if(i <= inCount - 1) {
		    		  lwBuffer.append("\n");
		    	  }
		      }
		      cCount += inCount; //FDE
		      System.out.println(tempStr = lwBuffer.toString());
			return tempStr;
		}
		public static String sub(String uInst) {
			String tempStr = "sub";
			for (int i = 0 ; i < 4 ; i++) {
		        String regStr = uInst.substring(rsIndex + (i*5), rtIndex + (i*5));
//		        System.out.println("i: " + i + " regStr: "  + regStr);
		        temp = Integer.parseInt(regStr);
		        regTemp = (int) iNumConv.binToDec(temp);
//		        regs[regTemp] = regTemp;
//		        System.out.println("currReg: " + regs[regTemp]);
		        if (i == 0)  {
		         rs = regTemp;
		        }
		        else if (i == 1) {
		         rt = regTemp;
		        }
		        else if (i == 2) {
		         rd = regTemp;
		        }
		        else if (i == 3) {
		         sh = regTemp;
		        }
		      }
			regs[rd] = regs[rs] - regs[rt];
			System.out.println("rs: " + rs + " rt: " + rt);
			System.out.println("rd: " + rd + " sh: " + sh);
			System.out.println("R" + rd + ": " + regs[rd]);
			
			String [] cyInst = {"IF", "ID", "EX", "WB"};
			StringBuffer lwBuffer = new StringBuffer();
			int inCount = 4;
			
			for (int i = 0 ; i < inCount ; i++) {
		    	  lwBuffer.append("C#" + (cCount+i) + " I" + pc + "-" + cyInst[i]);
		    	  if(i <= inCount - 1) {
		    		  lwBuffer.append("\n");
		    	  }
		      }
		      cCount += inCount; //FDEW
		      System.out.println(tempStr = lwBuffer.toString());
			return tempStr;
		}
}
