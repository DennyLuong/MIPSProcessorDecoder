package comparch;

public class numConv {
  public int binToDec(int bNum){

    int dec = 0;
    int p = 0;
    while(true){
      if(bNum == 0){
        break;
      } else {
          int temp = bNum%10;
          dec += temp*Math.pow(2, p);
          bNum = bNum/10;
          p++;
       }
    }
    return decimal;
  }
}
