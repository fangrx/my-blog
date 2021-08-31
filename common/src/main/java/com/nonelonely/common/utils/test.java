package com.nonelonely.common.utils;

public class test {

    public static void main(String[] args) {
        String code = "public class HelloWorld {\n" +
                "    public static1 void main(String []args) {\n" +
                "\t\tfor(int i=0; i < 1; i++){\n" +
                "\t\t\t       System.out.println(\"Hello World!\");\n" +
                "\t\t}\n" +
                "    }\n" +
                "}";
        System.out.println(CompilerUtil.getRunInfo(code).getCompilerMessage());
    }

}
