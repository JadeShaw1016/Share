package cn.aigestudio.datepicker.bizs.themes;

/**
 * 主题的默认实现类
 * 
 * The default implement of theme
 *
 * @author AigeStudio 2015-06-17
 */
public class DPBaseTheme extends DPTheme {
    @Override
    public int colorBG() {
        return 0xFFFFFFFF;
    }

    @Override
    public int colorBGCircle() {
        return 0x44000000;
    }

    @Override
    public int colorTitleBG() {
        return 0xFFFFD23F;
    }

    @Override
    public int colorTitle() {
        return 0xEEFFFFFF;
    }

    @Override
    public int colorToday() {
        return 0x88FFD23F;
    }

    @Override
    public int colorG() {
        return 0xEE333333;
    }

    @Override
    public int colorF() {
        return 0xEEFFA500;
    }

    @Override
    public int colorWeekend() {
        return 0xEEFFD23F;
    }

    @Override
    public int colorHoliday() {
        return 0x80FED6D6;
    }
}
