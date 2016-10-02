package info.bem.tools.bemplugin.cli.data;


public class BemBlock {
    private BemBlock(String version) {
        this.version = version;
    }
    public String version;

    public static BemBlock read(String input) {
        BemBlock bblock = new BemBlock(input);

        return bblock;
    }
}
