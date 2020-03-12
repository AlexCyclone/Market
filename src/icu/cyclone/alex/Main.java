package icu.cyclone.alex;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Processor processor = new Processor("./request", "./response", new Market());
        processor.process();
    }






}
