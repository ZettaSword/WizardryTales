package astramusfate.wizardry_tales.data.chanting;

public class SpellParam {

    private String name = "";
    private Number number = 1.0F;
    private String value = "";

    public SpellParam(String name){
        this.name = name;
    }

    public SpellParam(String name, float value){
        this.name = name;
        this.number = value;
    }

    public SpellParam(String name, String value){
        this.name = name;
        this.value = value;
    }

    public void setNumber(Number number) {
        this.number = number;
    }

    public Number getNumber(){
        return number;
    }
    public float num(){
        return number.floatValue();
    }

    public void setValue(String value) {
        this.value = value;
    }
    public String val(){
        return value;
    }

    public String name(){
        return name;
    }

    public boolean canApply(String value){
        return false;
    }

    public boolean canApply(Number number){
        return true;
    }
}
