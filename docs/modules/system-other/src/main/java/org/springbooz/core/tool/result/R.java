//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.springbooz.core.tool.result;

import io.swagger.annotations.ApiModel;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.boozsoft.utils.CollectOfUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ApiModel(
    value = "Result对象",
    description = "Result对象"
)
public class R<T> {
    public Long code;
    public String type;
    public String message;
    public Object result;
    public List<?> list;

    public static R ok() {
        return (new R()).setCode(0L).setMessage("ok").setType("success");
    }

    public static R ok(Object object) {
        return (new R()).setCode(0L).setResult(object).setMessage("ok").setType("success");
    }

    public static Mono<R> ok(Function<R, Mono> function) {
        R boozResult = (new R()).setCode(0L).setMessage("ok").setType("success");
        return (Mono)function.apply(boozResult);
    }

    public static R error() {
        return (new R()).setCode(-1L).setMessage("error").setType("error");
    }

    public static <T> Mono<T> just(T object) {
        return Mono.just(object);
    }

    public static Mono<R> ok(Flux all) {
        return all.collectList().map((list) -> {
            return (new R()).setCode(0L).setMessage("ok").setType("success").setResult(list);
        });
    }

    public static <T> R ok(List<T> list) {
        return (new R()).setCode(0L).setMessage("ok").setType("success").setResult(list);
    }

    public static <T> R page(List<T> list) {
        return (new R()).setCode(0L).setMessage("ok").setType("success").setResult(CollectOfUtils.mapof("total", list.size() == 0 ? 1 : list.size(), "items", list));
    }

    public static <T> R page(List<T> list,Long total) {
        return (new R()).setCode(0L).setMessage("ok").setType("success").setResult(CollectOfUtils.mapof("total",total, "items", list));
    }

    public static <T> R page(List<T> list, Pageable pageable,long total) {
        PageImpl page = new PageImpl(list, pageable, total);
        Map<String, Object> params = CollectOfUtils.mapof(
            "items", list,
            "page", page.getNumberOfElements(),
            "size", page.getSize(),
            "total", page.getTotalElements()
        );
        return (new R()).setCode(0L).setMessage("ok").setType("success").setResult(params);
    }

    public R() {
    }

    public Long getCode() {
        return this.code;
    }

    public String getType() {
        return this.type;
    }

    public String getMessage() {
        return this.message;
    }

    public Object getResult() {
        return this.result;
    }

    public List<?> getList() {
        return this.list;
    }

    public R<T> setCode(Long code) {
        this.code = code;
        return this;
    }

    public R<T> setType(String type) {
        this.type = type;
        return this;
    }

    public R<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public R<T> setResult(Object result) {
        this.result = result;
        return this;
    }

    public R<T> setList(List<?> list) {
        this.list = list;
        return this;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof R)) {
            return false;
        } else {
            R<?> other = (R)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label71: {
                    Object this$code = this.getCode();
                    Object other$code = other.getCode();
                    if (this$code == null) {
                        if (other$code == null) {
                            break label71;
                        }
                    } else if (this$code.equals(other$code)) {
                        break label71;
                    }

                    return false;
                }

                Object this$type = this.getType();
                Object other$type = other.getType();
                if (this$type == null) {
                    if (other$type != null) {
                        return false;
                    }
                } else if (!this$type.equals(other$type)) {
                    return false;
                }

                label57: {
                    Object this$message = this.getMessage();
                    Object other$message = other.getMessage();
                    if (this$message == null) {
                        if (other$message == null) {
                            break label57;
                        }
                    } else if (this$message.equals(other$message)) {
                        break label57;
                    }

                    return false;
                }

                Object this$result = this.getResult();
                Object other$result = other.getResult();
                if (this$result == null) {
                    if (other$result != null) {
                        return false;
                    }
                } else if (!this$result.equals(other$result)) {
                    return false;
                }

                Object this$list = this.getList();
                Object other$list = other.getList();
                if (this$list == null) {
                    if (other$list == null) {
                        return true;
                    }
                } else if (this$list.equals(other$list)) {
                    return true;
                }

                return false;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof R;
    }



    public String toString() {
        Long var10000 = this.getCode();
        return "R(code=" + var10000 + ", type=" + this.getType() + ", message=" + this.getMessage() + ", result=" + this.getResult() + ", list=" + this.getList() + ")";
    }
}
