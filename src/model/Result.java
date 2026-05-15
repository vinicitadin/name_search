package model;

public class Result {
    private String fileName;
    private int lineNumber;
    private String lineContent;

    public Result(String fileName, int lineNumber, String lineContent) {
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.lineContent = lineContent;
    }

    public String getFileName() {
        return fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getLineContent() {
        return lineContent;
    }

    @Override
    public String toString() {
        return String.format("Arquivo: %s | Linha %d: %s", fileName, lineNumber, lineContent);
    }
}