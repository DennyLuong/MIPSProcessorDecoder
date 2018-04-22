package comparch;

public class Converter {
  public long binToDec(long bNum){

    long dec = 0;
    int p = 0;
    while(true){
      if(bNum == 0){
        break;
      } else {
          long temp = bNum%10;
          dec += temp*Math.pow(2, p);
          bNum = bNum/10;
          p++;
       }
    }
    return dec;
  }
}
