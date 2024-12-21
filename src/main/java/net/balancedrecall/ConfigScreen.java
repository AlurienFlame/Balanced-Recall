package net.balancedrecall;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {
    private Screen parent;
	private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);

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

		layout.addHeader(new TextWidget(Text.of("Balanced Recall Configuration"), this.textRenderer));

		// Mirror settings
		column.add(CheckboxWidget.builder(Text.of("Taking damage interrupts recall"), this.textRenderer).checked(true).build());
		column.add(CheckboxWidget.builder(Text.of("Taking damage puts mirror on cooldown"), this.textRenderer).checked(true).build());
		column.add(CheckboxWidget.builder(Text.of("Recalling is impossible when mosters are nearby"), this.textRenderer).checked(true).build());

		GridWidget grid = new GridWidget();
		grid.getMainPositioner().margin(4).alignHorizontalCenter();
		GridWidget.Adder adder = grid.createAdder(2);
		adder.getMainPositioner().alignLeft().alignVerticalCenter();
		adder.add(new TextWidget(Text.of("Magic Mirror Use Time"), this.textRenderer));
		adder.add(new TextFieldWidget(this.textRenderer, 100, 20, Text.of("Magic Mirror Use Time")));
		adder.add(new TextWidget(Text.of("Magic Mirror Cooldown Time"), this.textRenderer));
		adder.add(new TextFieldWidget(this.textRenderer, 100, 20, Text.of("Magic Mirror Cooldown Time")));
		adder.add(new TextWidget(Text.of("Dimensional Mirror Use Time"), this.textRenderer));
		adder.add(new TextFieldWidget(this.textRenderer, 100, 20, Text.of("Dimensional Mirror Use Time")));
		adder.add(new TextWidget(Text.of("Dimensional Mirror Cooldown Time"), this.textRenderer));
		adder.add(new TextFieldWidget(this.textRenderer, 100, 20, Text.of("Dimensional Mirror Cooldown Time")));
		column.add(grid);

		// Recall settings
		column.add(CheckboxWidget.builder(Text.of("Sleeping mat resets phantom timer"), this.textRenderer).build());

		this.layout.addBody(column);

		// Config screen buttons
		this.layout.addFooter(ButtonWidget.builder(Text.of("Done"), (btn) -> this.close()).dimensions(40, 40, 120, 20).build());

		this.layout.forEachChild(child -> {
			this.addDrawableChild(child);
		});
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
