package Cluedo;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class ReadImage {
    public static void main(String[] args) {


        try {
        File originalFile = new File("./assets/Animations/01.gif");//指定要读取的图片
        FileInputStream in = new FileInputStream(originalFile);


            File result = new File("./assets/Animations/011.gif");//要写入的图片
            if (result.exists()) {//校验该文件是否已存在
                result.delete();//删除对应的文件，从磁盘中删除
                result = new File("./assets/Animations/011.gif");//只是创建了一个File对象，并没有在磁盘下创建文件
            }
            else if (!result.exists()) {//如果文件不存在
                result.createNewFile();//会在磁盘下创建文件，但此时大小为0K
            }

            FileOutputStream out = new FileOutputStream(result);// 指定要写入的图片
            int n = 0;// 每次读取的字节长度
            byte[] bb = new byte[1024];// 存储每次读取的内容
            while ((n = in.read(bb)) != -1) {
                out.write(bb, 0, n);// 将读取的内容，写入到输出流当中
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        finally{
//            //执行完以上后，磁盘下的该文件才完整，大小是实际大小
//            out.close();// 关闭输入输出流
//            in.close();
//        }








        // image

//        try{
//            BufferedImage bufImage = ImageIO.read(new File("./assets/Animations/1.jpg"));
//            int width = bufImage.getWidth();
//            int height = bufImage.getHeight();
//            for (int j = 0; j < height; j++) {
//                for (int i = 0; i < width; i++) {
//                    int p = bufImage.getRGB(i, j);
////                    int a = (p >> 24) & 0xff;
////                    int r = (p >> 16) & 0xff;
////                    int g = (p >> 8) & 0xff;
////                    int b = p & 0xff;
////
////                    p = (a << 24) | (r << 16) | (g << 8) | b;
////
////                    bufImage.setRGB(i, j, p);
//                    System.out.println(p);
//                }
//            }






//        }catch(IOException e){
//            throw new Error(" ! ");
//        }




    }





}
