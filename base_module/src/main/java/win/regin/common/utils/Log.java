package win.regin.common.utils;

import android.os.Environment;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
    public static boolean DEBUG;
    public static boolean WRITE_TO_FILE = false;
    private static final String LOG_NAME = "gacha.log";
    private static File logFile;
    private static int TYPE_DEBUG = 0;
    private static int TYPE_ERROR = 1;
    private static int TYPE_INFO = 2;
    private static int TYPE_WARNING = 3;

    private Log() {
    }
    public static void isDebug(boolean isDebug) {
        DEBUG = isDebug;
    }

    public static void d(String log) {
        if (DEBUG) {
            Log("d", TYPE_DEBUG + "：" + log);
            if (WRITE_TO_FILE) {
                String time = time();
                write_log(time + " [D][" + TYPE_DEBUG + "]" + log);
            }
        }
    }

    public static final void d(String LOG_TAG, String log) {
        if (DEBUG) {
            Log("d", LOG_TAG + "：" + log);
            if (WRITE_TO_FILE) {
                String time = time();
                write_log(time + " [D][" + LOG_TAG + "]" + log);
            }
        }
    }

    public static final void e(String log) {
        if (DEBUG) {
            Log("e", TYPE_ERROR + "：" + log);
            if (WRITE_TO_FILE) {
                String time = time();
                write_log(time + " [E][" + TYPE_ERROR + "]" + log);
            }
        }
    }

    public static final void e(String LOG_TAG, String log) {
        if (DEBUG) {
            Log("e", LOG_TAG + "：" + log);
            if (WRITE_TO_FILE) {
                String time = time();
                write_log(time + " [E][" + LOG_TAG + "]" + log);
            }
        }
    }

    public static final void i(String LOG_TAG, String log) {
        if (DEBUG) {
            Log("i", LOG_TAG + "：" + log);
            if (WRITE_TO_FILE) {
                String time = time();
                write_log(time + " [I][" + LOG_TAG + "]" + log);
            }
        }
    }

    public static final void i(String log) {
        if (DEBUG) {
            Log("i", TYPE_INFO + "：" + log);
            if (WRITE_TO_FILE) {
                String time = time();
                write_log(time + " [I][" + TYPE_INFO + "]" + log);
            }
        }
    }

    public static final void v(String LOG_TAG, String log) {
        if (DEBUG) {
            Log("v", LOG_TAG + "：" + log);
            if (WRITE_TO_FILE) {
                String time = time();
                write_log(time + " [W][" + LOG_TAG + "]" + log);
            }
        }
    }

    public static final void w(String LOG_TAG, String log) {
        if (DEBUG) {
            Log("w", LOG_TAG + "：" + log);
            if (WRITE_TO_FILE) {
                String time = time();
                write_log(time + " [W][" + LOG_TAG + "]" + log);
            }
        }
    }


    public static final void writeLog(String LOG_TAG, String log) {
        Logger.w(log);
        String time = time();
        write_log(time + " [W][" + LOG_TAG + "]" + log);
    }

    //读取写入的log信息
    public static final String readErrorLog() {
        return read_log();
    }


    public static final void logJson(String json) {
        Log("d", "JSON" + "：" + json);
    }


    public static void Log(String type, String msg) {  //信息太长,分段打印
        //因为String的length是字符数量不是字节数量所以为了防止中文字符过多，
        //  把4*1024的MAX字节打印长度改为2001字符数
        int max_str_length = 2001;
        msg = msg.replaceAll("http", "oqs");
        //大于4000时
        while (msg.length() > max_str_length) {
            String ss = msg.substring(0, max_str_length);
            if (type.equals("i")) {
                Logger.i(ss);
            } else if (type.equals("w")) {
                Logger.w(ss);
            } else if (type.equals("e")) {
                Logger.e(ss);
            } else if (type.equals("v")) {
                Logger.v(ss);
            } else {
                Logger.d(ss);
            }

            msg = msg.substring(max_str_length);
        }
        //剩余部分

        if (type.equals("i")) {
            Logger.i(msg);
        } else if (type.equals("w")) {
            Logger.w(msg);
        } else if (type.equals("e")) {
            Logger.e(msg);
        } else if (type.equals("v")) {
            Logger.v(msg);
        } else {
            Logger.d(msg);
        }

    }


    //删除信息
    public static final void clearErrorLog() {
        try {
            logFile.delete();
        } catch (Exception ex) {
        }
    }

    public static void initLog() {

        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(0)         // (Optional) How many method line to show. Default 2
                .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
//                .logStrategy(LogStrategy) // (Optional) Changes the log strategy to print out. Default LogCat
                .tag("Oqs")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();

        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));

        String folderString = Constants.APP_DIR;
        String dir = Environment.getExternalStorageDirectory() + "/gacha/logs";
        File folder = new File(dir + folderString);
        File imageCache = new File(dir + Constants.APP_PICTURES);
        File toysCache = new File(dir + Constants.APP_CACHE);
        File cache = new File(dir + Constants.HTTP_CACHE);
        if (!folder.exists() || !imageCache.exists() || !cache.exists() || !toysCache.exists()) {
            try {
                //按照指定的路径创建文件夹
                folder.mkdirs();
                imageCache.mkdirs();
                cache.mkdirs();
                toysCache.mkdirs();
                /*folderString = Constants.APP_PICTURES;
                File file = new File(folderString);
                if(!file.exists()) {
                    file.mkdir();
                }*/
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        logFile = new File(folder.getAbsolutePath(), LOG_NAME);
        if (!logFile.exists()) {
            try {
                //在指定的文件夹中创建文件
                logFile.createNewFile();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    private static String read_log() {
        StringBuilder builder = new StringBuilder();
        try {
            FileReader file = new FileReader(logFile);
            char[] chs = new char[1024];
            while ((file.read(chs)) != -1) {
                builder.append(chs);
            }
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return builder.toString();
    }


    private static void write_log(String log) {
        try {
            FileWriter file = new FileWriter(logFile, true);
            log = log + "\r\n\n";
            file.write(log, 0, log.length());
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String time() {
        SimpleDateFormat dateformat1 = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss ");
        String a1 = dateformat1.format(new Date());
        return a1;
    }
}
