package jsoup;

import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.*;


import java.io.*;

public class pchong {
    public static void downImages(String filePath, String imgUrl, String dataID) {
        // 若指定文件夹没有，则先创建
        File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 写出的路径，设置成楼房的ID号为楼房图片命名
        File file = new File(filePath + File.separator + dataID + ".jpg");

        try {
            // 获取图片URL
            URL url = new URL(imgUrl);
            // 获得连接
            URLConnection connection = url.openConnection();
            // 设置10秒的相应时间
            connection.setConnectTimeout(10 * 1000);
            // 获得输入流
            InputStream in = connection.getInputStream();
            // 获得输出流
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
            // 构建缓冲区
            byte[] buf = new byte[1024];
            int size;
            // 写入到文件
            while (-1 != (size = in.read(buf))) {
                out.write(buf, 0, size);
            }
            out.close();
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Connection sqlcon = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("找不到驱动程序类，加载驱动失败");
            e.printStackTrace();
        }
        String docurl = "jdbc:mysql://localhost:3306/test?serverTimezone=GMT%2B8";
        String username = "root";
        String password = "123456";
        try {
            sqlcon = DriverManager.getConnection(docurl, username, password);
        } catch (SQLException e) {
            System.out.println("数据库连接失败");
            e.printStackTrace();
        }
        try {
            long starTime = System.currentTimeMillis();
            int pageNum = 0;
            Writer w = new FileWriter("G:/爬虫/pcongtext.txt", true);
            while (true) {
                pageNum++;

                String url = "https://sz.diandianzu.com/listing/p" + pageNum;
                Document doc = Jsoup.connect(url).timeout(500000).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31").get();
                if (doc == null) {
                    continue;
                }
                Elements data = doc.getElementsByClass("list-main");
                Elements dataIdList = data.select("[data-id]");
                if (dataIdList == null || dataIdList.size() <= 0) {
                    break;
                }
                for (Element dataIdElement : dataIdList) {
                    String dataId = dataIdElement.attr("data-id");
                    System.out.print("写字楼id:" + dataId + "  ");
                    String newUrl = "https://sz.diandianzu.com/listing/detail-i" + dataId + ".html";
                    Document newDoc = Jsoup.connect(newUrl).timeout(500000).userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36").get();
                    Elements bdn = newDoc.getElementsByClass("top-title");
                    String buildingName = bdn.text();
                    System.out.println(buildingName);
                    String writetext = "写字楼id:" + dataId + "  " + buildingName;
                    w.write(writetext + "\r\n");
                    /*
                     *
                     * 爬取图片部分
                     *
                     * */
                    Elements buildingTag = newDoc.getElementsByClass("swiper-slide");
                    Element img = buildingTag.select("img").first();
                    System.out.println("共检测到下列图片URL：");
                    System.out.println("开始下载");
                    //获取每个img标签URL "abs:"表示绝对路径
                    String imgSrc = img.attr("abs:src");
                    // 打印URL
                    System.out.println(imgSrc);
                    //下载图片到本地
                    pchong.downImages("G:/爬虫/img1", imgSrc, dataId);
                    System.out.println("下载完成");
                    /*
                     *
                     * 写进数据库
                     *
                     * */
                    /*
                    String sql = "INSERT INTO building(id,name) VALUES(?,?)";
                    PreparedStatement pstm = sqlcon.prepareStatement(sql);
                    pstm.setString(1,dataId);
                    pstm.setString(2,buildingName);
                    int rows = pstm.executeUpdate();
                    if (rows > 0) {
                        System.out.println("successfully");
                    }
                    */
                }
            }
//            sqlcon.close();
            w.close();
            long endTime = System.currentTimeMillis();
            System.out.println("共访问网页数量：" + pageNum);
            System.out.println("共耗时：" + (endTime - starTime) / 1000 + "s");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
