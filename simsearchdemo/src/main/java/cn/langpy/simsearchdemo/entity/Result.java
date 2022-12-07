package cn.langpy.simsearchdemo.entity;


public class Result<T> {
    private Integer state = 1;
    private String message;
    private T content;


    public static Result failed(String message) {
        Result result = new Result();
        result.setState(0);
        result.setMessage(message);
        return result;
    }

    public static Result success(Object content) {
        Result result = new Result();
        result.setState(1);
        result.setMessage("成功");
        result.setContent(content);
        return result;
    }
    public static Result success() {
        Result result = new Result();
        result.setState(1);
        result.setMessage("成功");
        return result;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}
