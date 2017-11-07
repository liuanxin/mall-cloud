package com.github.common;


import com.fasterxml.jackson.annotation.JsonValue;
import com.github.common.util.U;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;

public class Money {
    /** 元分换算的比例. 左移右移的位数 */
    private static final int SCALE = 2;

    /** 实际存储进数据库的值 */
    private Long cent;

    public Money() {}
    /** 从 web 前台过来的数据, 使用此构造 */
    public Money(String yuan) {
        cent = yuan2Cent(yuan);
    }
    /** 从数据库过来的数据, 使用此构造 */
    public Money(Long cent) {
        this.cent = cent;
    }

    public Long getCent() {
        return cent;
    }
    public void setCent(Long cent) {
        this.cent = cent;
    }


    // 运算全部基于 long 来做, 它的性能比 BigDecimal 要好很多, 只有在 除法 的地方会出现精度问题而且是完全可以接受的
    public Money plus(Money money) {
        return new Money(cent + money.cent);
    }
    public Money plusSelf(Money money) {
        cent += money.cent;
        return this;
    }

    public Money less(Money money) {
        return new Money(cent - money.cent);
    }
    public Money lessSelf(Money money) {
        cent -= money.cent;
        return this;
    }

    public Money multiply(int num) {
        return new Money(cent * num);
    }
    public Money multiplySelf(int num) {
        cent *= num;
        return this;
    }

    public Money divide(int num) {
        return new Money(cent / num);
    }
    public Money divideSelf(int num) {
        cent /= num;
        return this;
    }
    // 链式运算时, 如 Money count = xxx.plus(y).lessSelf(z).multiplySelf(n).divideSelf(m) 这种
    // 第一次不要用 self, 后面的都加上 self, 这样只会实例化一次.
    // 如果都不带 self 则每一次运算都将实例化一个 Money 对象
    // 如果都带 self 将会改变第一个调用的值, 如上面的示例如果每次调用都带 self 改变的其实是 xxx 的值


    /** 检查金额是否是负数 */
    public void checkNegative() {
        U.assertException(cent == null || cent < 0, "金额不能是负数");
    }

    /** 在前台或者在页面上显示 */
    @JsonValue
    @Override
    public String toString() {
        return cent2Yuan(cent);
    }

    private static Long yuan2Cent(String yuan) {
        // 元转换为分
        if (U.isBlank(yuan)) {
            return null;
        } else {
            try {
                Double.parseDouble(yuan);
            } catch (NumberFormatException e) {
                U.assertException(true, String.format("不是有效的金额(%s)", yuan));
            }
            return new BigDecimal(yuan).movePointRight(SCALE).longValue();
        }
    }
    private static String cent2Yuan(Long cent) {
        // 分转换为元
        return U.greater0(cent) ? BigDecimal.valueOf(cent).movePointLeft(SCALE).toString() : U.EMPTY;
    }

    /** 输出大写中文 */
    public String toChinese() {
        return ChineseConvert.upperCase(toString());
    }


    /** 转换中文大写 */
    private static final class ChineseConvert {
        /** 整数和小数位之间的分隔 */
        private static final String SPLIT = " ";
        private static final String WHOLE = "整";
        private static final String NEGATIVE = "负";

        private static final String[] INTEGER = {
                "圆", "拾", "佰", "仟",
                "万", "拾", "佰", "仟",
                "亿", "拾", "佰", "仟"
        };
        private static final String[] DECIMAL = {/*"厘", */"分", "角"};
        private static final String[] NUM = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};

        /**
         * 转换大写
         *
         * @param money 金额
         * @return 大写
         */
        private static String upperCase(String money) {
            // 必要的检查. 如果是 0 直接返回
            if (money == null || money.trim().length() == 0) {
                return "不是有效的金额";
            }
            try {
                if (Double.parseDouble(money) == 0) {
                    return NUM[0] + INTEGER[0] + WHOLE;
                }
            } catch (NumberFormatException nfe) {
                return "不是有效的金额";
            }

            // 基本的位数检查, 按小数拆分, 分别处理
            String left = money.contains(".") ? money.substring(0, money.indexOf(".")) : money;
            long leftLong = NumberUtils.toLong(left);
            if (leftLong < 0) {
                left = left.substring(1);
            }
            if (left.length() > INTEGER.length) {
                return "最大只能转换到小数位前 " + INTEGER.length + " 位";
            }
            String right = money.contains(".") ? money.substring(money.indexOf(".") + 1) : "";
            if (right.length() > DECIMAL.length) {
                right = right.substring(0, DECIMAL.length);
                // return "最小只能转换到小数位后 " + DECIMAL.length + " 位(" + DECIMAL[0] + ")";
            }

            StringBuilder sbd = new StringBuilder();
            // 处理小数位前面的数
            if (leftLong != 0) {
                if (leftLong < 0) {
                    sbd.append(NEGATIVE);
                }
                for (int i = 0; i < left.length(); i++) {
                    int number = NumberUtils.toInt(String.valueOf(left.charAt(i)));
                    sbd.append(NUM[number]).append(INTEGER[left.length() - i - 1]);
                }
            }

            // 处理小数位后面的值
            long rightLong = NumberUtils.toLong(right);
            if (rightLong > 0) {
                sbd.append(SPLIT);
                for (int i = 0; i < right.length(); i++) {
                    int number = NumberUtils.toInt(String.valueOf(right.charAt(i)));
                    sbd.append(NUM[number]).append(DECIMAL[right.length() - i - 1]);
                }
            } else if (rightLong == 0) {
                sbd.append(WHOLE);
            }
            // 最后的收尾工作, 替换成可读性更强的.
            return sbd.toString().replaceAll("零仟", "零").replaceAll("零佰", "零").replaceAll("零拾", "零")
                    .replaceAll("零零零", "零").replaceAll("零零", "零")
                    .replaceAll("零亿", "亿").replaceAll("零万", "万").replaceAll("亿万", "亿")
                    .replaceAll("壹拾", "拾").replaceAll("零圆", "圆")
                    .replaceAll("零角", "").replaceAll("零分", "");
        }
    }
}
