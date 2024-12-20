package net.balancedrecall;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {
    private Screen parent;
	private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this, 61, 33);

	public ConfigScreen(@Nullable Screen parent) {
        super(Text.translatable("balancedrecall.menu.title"));

		this.parent = parent;

		// TODO: Load config

		// TODO: Reset button

    }

	@Override
	protected void init() {
		// TODO: lang file instead of hardcoded strings
		// TODO: write config to file
		// TODO: detect config in real code

		DirectionalLayoutWidget column = DirectionalLayoutWidget.vertical().spacing(8);
		column.getMainPositioner().alignHorizontalCenter();

		// Mirror settings
		column.add(CheckboxWidget.builder(Text.of("Taking damage interrupts recall"), this.textRenderer).checked(true).build());
		column.add(CheckboxWidget.builder(Text.of("Taking damage puts mirror on cooldown"), this.textRenderer).checked(true).build());
		column.add(CheckboxWidget.builder(Text.of("Recalling is impossible when mosters are nearby"), this.textRenderer).checked(true).build());

		// column.add(new TextWidget(Text.of("Magic Mirror Use Time"), textRenderer));
		column.add(new TextFieldWidget(textRenderer, 100, 20, Text.of("Magic Mirror Use Time")));
		column.add(new TextFieldWidget(textRenderer, 100, 20, Text.of("Magic Mirror Cooldown Time")));
		column.add(new TextFieldWidget(textRenderer, 100, 20, Text.of("Dimensional Mirror Use Time")));
		column.add(new TextFieldWidget(textRenderer, 100, 20, Text.of("Dimensional Mirror Cooldown Time")));

		// Recall settings
		column.add(CheckboxWidget.builder(Text.of("Sleeping mat resets phantom timer"), this.textRenderer).build());

		// Config screen buttons
		column.add(ButtonWidget.builder(Text.of("Done"), (btn) -> this.close()).dimensions(40, 40, 120, 20).build());

		column.forEachChild(child -> {
			this.addDrawableChild(child);
		});
		this.layout.addBody(column);
		this.refreshWidgetPositions();
	}

	@Override
	protected void refreshWidgetPositions() {
		this.layout.refreshPositions();
	}

	@Override
	public void close() {
		this.client.setScreen(this.parent);
	}
}
