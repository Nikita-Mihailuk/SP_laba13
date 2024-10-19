import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        Scanner in = new Scanner(System.in);
        String url = "";
        while (true){
            System.out.println("Введите ссылку для скачивания файла(введите 0 для выхода из программы): ");

            url = in.nextLine();
            String finalUrl = url;
            if (url.equals("0")){
                break;
            }
            new Thread(() -> DownloadURL(finalUrl)).start();
        }
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