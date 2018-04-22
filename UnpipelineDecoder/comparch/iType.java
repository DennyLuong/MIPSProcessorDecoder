package comparch;

public class iType {
  int rsIndex = 6;
  int rtIndex = 11;
  int immIndex = 16;

  int rs;
  int rt;
  long imm;

	int[] regs = new int[32];
  int temp = 0;
  int regTemp = 0;
  Converter iNumConv = new Converter();
	public String addi(String uInst) {
		String tempStr = "addi";
      for (int i = 0 ; i < 2 ; i++) {
        String regStr = uInst.substring(rsIndex + (i*5), rtIndex + (i*5));
        System.out.println("i: " + i + " regStr: "  + regStr);
        temp = Integer.parseInt(regStr);
        regTemp = (int) iNumConv.binToDec(temp);
        regs[regTemp] = regTemp;
        System.out.println("currReg: " + regs[regTemp]);
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


		return tempStr;
	}
	public String beq() {
		String tempStr = "beq";

		return tempStr;
	}
	public String bne() {
		String tempStr = "bne";

		return tempStr;
	}
	public String lw(String uInst) {
		String tempStr = "lw";
		 for (int i = 0 ; i < 2 ; i++) {
		        String regStr = uInst.substring(rsIndex + (i*5), rtIndex + (i*5));
		        System.out.println("i: " + i + " regStr: "  + regStr);
		        temp = Integer.parseInt(regStr);
		        regTemp = (int) iNumConv.binToDec(temp);
		        regs[regTemp] = regTemp;
		        System.out.println("currReg: " + regs[regTemp]);
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
    return tempStr;
	}
	public String sw() {
		String tempStr = "sw";

		return tempStr;
	}
}
