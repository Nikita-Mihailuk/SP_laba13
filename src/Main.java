import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args){

        // Последовательное скачивание
        DownloadURL("https://videocdn.cdnpk.net/joy/content/video/free/2014-12/large_preview/Raindrops_Videvo.mp4?token=exp=1729269128~hmac=f0c686b39913fb54c0b3110fe3fa5acb595a510c7d1d7f38b4ae11dfead72c8f");
        DownloadURL("https://oreol-tour.ru/files/Trebovanie_obrazec.pdf");

        // Параллельное скачивание
        new Thread(() -> DownloadURL("https://rus.hitmotop.com/get/music/20190803/So_slovami_-_Gimn_Rossii_65864310.mp3")).start();
        new Thread(() -> DownloadURL("https://sun9-38.userapi.com/impg/RVdX7bELiniswNhB60ELLHiWPobf2krletzdVw/WtqpwA6HRSc.jpg?size=720x1280&quality=95&sign=303e1d0d59a5795c485111fe4b993446&type=album")).start();
        new Thread(() -> DownloadURL("https://www.pngmart.com/files/11/Derp-Face-Meme-Transparent-PNG.png")).start();

    }

    public static void DownloadURL(String URL){
        try {
            URLConnection conn = new URL(URL).openConnection();
            InputStream is = conn.getInputStream();

            // Чтение первых 3 байтов для определения типа файла
            byte[] header = new byte[3];
            is.read(header);

            // Определение расширения по первым 3 байтам
            String extension = getExtension(header);

            if (extension == null) {
                System.out.println("Не удалось определить тип файла.");
                return;
            }

            // Формируем полный путь с расширением
            String fullPath = "file" + extension;

            OutputStream os = new FileOutputStream(fullPath);

            os.write(header); // Сначала пишем заголовок
            os.write(is.readAllBytes()); // Затем весь оставшийся файл

            System.out.println("Файл скачан: " + fullPath);

            // Открываем файл
            Desktop.getDesktop().open(new File(fullPath));

            // Освобождаем ресурсы
            os.close();
            is.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Метод для определения расширения по первым 3 байтам
    public static String getExtension(byte[] header) {

        Map<String, String> Extensions = new HashMap<>();
        Extensions.put("FF D8 FF", ".jpg");
        Extensions.put("89 50 4E", ".png");
        Extensions.put("49 44 33", ".mp3");
        Extensions.put("25 50 44", ".pdf");
        Extensions.put("00 00 00", ".mp4");
        Extensions.put("50 4B 03", ".docx");
        Extensions.put("47 49 46", ".gif");
        Extensions.put("50 4B 07", ".zip");
        Extensions.put("52 61 72", ".rar");

        // Преобразуем первые байты в строку шестнадцатиричных чисел
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) { // Считаем первые 3 байта
            sb.append(String.format("%02X ", header[i])); // %02X означает 16-тиричную систему
        }

        // Преобразуем в строку для дальнейшей работы и удаляем пробел на конце, потому что символы прибавляются с пробелом
        String str = sb.toString().trim();

        // Возвращаем расширение, если нашли соответствие, если нет, то возвращаем null
        return Extensions.getOrDefault(str, null);
    }
}