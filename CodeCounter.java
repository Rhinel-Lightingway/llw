import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

public class CodeCounter {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("请提供目录路径。");
            return;
        }
        String dirPath = args[0];
        Path path = Paths.get(dirPath);
        if (!Files.isDirectory(path)) {
            System.out.println("无效的目录路径。");
            return;
        }

        // 总计数器
        AtomicInteger totalCodeLines = new AtomicInteger(0);
        AtomicInteger totalCommentLines = new AtomicInteger(0);
        AtomicInteger totalBlankLines = new AtomicInteger(0);

        try {
            Files.walk(path)
                .filter(p -> p.toString().endsWith(".java"))
                .forEach(p -> {
                    try {
                        BufferedReader reader = Files.newBufferedReader(p, StandardCharsets.UTF_8);
                        String line;
                        boolean inBlockComment = false; // 追踪是否在多行注释中
                        while ((line = reader.readLine()) != null) {
                            LineType lineType = processLine(line, inBlockComment);
                            if (lineType.isCodeLine) totalCodeLines.incrementAndGet();
                            if (lineType.isCommentLine) totalCommentLines.incrementAndGet();
                            if (lineType.isBlankLine) totalBlankLines.incrementAndGet();
                            inBlockComment = lineType.inBlockComment;
                        }
                        reader.close();
                    } catch (IOException e) {
                        System.out.println("无法读取文件: " + p.toString());
                    }
                });

            // 打印结果
            System.out.println("代码行数: " + totalCodeLines.get());
            System.out.println("注释行数: " + totalCommentLines.get());
            System.out.println("空白行数: " + totalBlankLines.get());

        } catch (IOException e) {
            System.out.println("处理目录失败: " + dirPath);
        }
    }

    static class LineType {
        boolean isCodeLine;
        boolean isCommentLine;
        boolean isBlankLine;
        boolean inBlockComment;

        LineType(boolean code, boolean comment, boolean blank, boolean inBlock) {
            isCodeLine = code;
            isCommentLine = comment;
            isBlankLine = blank;
            inBlockComment = inBlock;
        }
    }

    private static LineType processLine(String line, boolean inBlockComment) {
        boolean isCodeLine = false;
        boolean isCommentLine = false;
        boolean isBlankLine = false;
        boolean inString = false;
        boolean inChar = false;
        boolean escape = false;

        String trimmedLine = line.trim();
        if (trimmedLine.isEmpty()) {
            isBlankLine = true;
        }

        int index = 0;
        int length = line.length();

        while (index < length) {
            char c = line.charAt(index);
            // 检查是否在多行注释中
            if (inBlockComment) {
                isCommentLine = true;
                if (c == '*' && index + 1 < length && line.charAt(index + 1) == '/') {
                    inBlockComment = false;
                    index += 2;
                } else {
                    index++;
                }
            } else if (inString) {
                // 处理字符串内部
                if (c == '\\' && !escape) {
                    escape = true;
                } else if (c == '"' && !escape) {
                    inString = false;
                } else {
                    escape = false;
                }
                index++;
            } else if (inChar) {
                // 处理字符内部
                if (c == '\\' && !escape) {
                    escape = true;
                } else if (c == '\'' && !escape) {
                    inChar = false;
                } else {
                    escape = false;
                }
                index++;
            } else {
                if (c == '/') {
                    if (index + 1 < length) {
                        char nextChar = line.charAt(index + 1);
                        if (nextChar == '/') {
                            // 单行注释
                            isCommentLine = true;
                            break; // 剩余部分都是注释
                        } else if (nextChar == '*') {
                            // 多行注释开始
                            inBlockComment = true;
                            isCommentLine = true;
                            index += 2;
                        } else {
                            isCodeLine = true;
                            index++;
                        }
                    } else {
                        isCodeLine = true;
                        index++;
                    }
                } else if (c == '"' ) {
                    inString = true;
                    isCodeLine = true;
                    index++;
                } else if (c == '\'') {
                    inChar = true;
                    isCodeLine = true;
                    index++;
                } else if (Character.isWhitespace(c)) {
                    index++;
                } else {
                    isCodeLine = true;
                    index++;
                }
            }
        }

        // 如果在多行注释中，且行为空白，则计为空白行和注释行
        if (trimmedLine.isEmpty() && inBlockComment) {
            isBlankLine = true;
            isCommentLine = true;
        }

        return new LineType(isCodeLine, isCommentLine, isBlankLine, inBlockComment);
    }
}
