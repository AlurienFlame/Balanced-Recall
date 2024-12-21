package net.balancedrecall;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {
    private Screen parent;
	private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
	private final BalancedRecallConfig config;

	public ConfigScreen(@Nullable Screen parent) {
        super(Text.translatable("balancedrecall.menu.title"));
		this.parent = parent;
		this.config = BalancedRecall.config;
    }

	@Override
	protected void init() {
		// TODO: lang file instead of hardcoded strings
		// TODO: detect config in real code

		DirectionalLayoutWidget column = DirectionalLayoutWidget.vertical().spacing(8);

		layout.addHeader(new TextWidget(Text.of("Balanced Recall Configuration"), this.textRenderer));

		// Mirror settings
		column.add(
			CheckboxWidget
			.builder(Text.of("Taking damage interrupts recall"), this.textRenderer)
			.checked(config.getBoolean("take_damage_interrupts_recall"))
			.callback((checkbox, checked)->config.set("take_damage_interrupts_recall", checked))
			.build()
		);
		column.add(
			CheckboxWidget
			.builder(Text.of("Taking damage puts mirror on cooldown"), this.textRenderer)
			.checked(config.getBoolean("take_damage_puts_mirror_on_cooldown"))
			.callback((checkbox, checked)->config.set("take_damage_puts_mirror_on_cooldown", checked))
			.build()
		);
		column.add(
			CheckboxWidget
			.builder(Text.of("Recalling is impossible when mosters are nearby"), this.textRenderer)
			.checked(config.getBoolean("recall_impossible_when_monsters_nearby"))
			.callback((checkbox, checked)->config.set("recall_impossible_when_monsters_nearby", checked))
			.build()
		);

		GridWidget grid = new GridWidget();
		grid.getMainPositioner().margin(4).alignHorizontalCenter();
		GridWidget.Adder adder = grid.createAdder(2);
		adder.getMainPositioner().alignLeft().alignVerticalCenter();

		adder.add(new TextWidget(Text.of("Magic Mirror Use Time"), this.textRenderer));
		TextFieldWidget magicMirrorUseTime = new TextFieldWidget(
			this.textRenderer,
			100,
			20,
			Text.of("Magic Mirror Use Time")
		);
		magicMirrorUseTime.setChangedListener((value)->config.set("magic_mirror_use_time_ticks", value));
		magicMirrorUseTime.setText(config.getInteger("magic_mirror_use_time_ticks"));
		adder.add(magicMirrorUseTime);

		adder.add(new TextWidget(Text.of("Magic Mirror Cooldown Time"), this.textRenderer));
		TextFieldWidget magicMirrorCooldownTime = new TextFieldWidget(
			this.textRenderer,
			100,
			20,
			Text.of("Magic Mirror Cooldown Time")
		);
		magicMirrorCooldownTime.setChangedListener((value)->config.set("magic_mirror_cooldown_time_seconds", value));
		magicMirrorCooldownTime.setText(config.getInteger("magic_mirror_cooldown_time_seconds"));
		adder.add(magicMirrorCooldownTime);

		adder.add(new TextWidget(Text.of("Dimensional Mirror Use Time"), this.textRenderer));
		TextFieldWidget dimensionalMirrorUseTime = new TextFieldWidget(
			this.textRenderer,
			100,
			20,
			Text.of("Dimensional Mirror Use Time")
		);
		dimensionalMirrorUseTime.setChangedListener((value)->config.set("dimensional_mirror_use_time_ticks", value));
		dimensionalMirrorUseTime.setText(config.getInteger("dimensional_mirror_use_time_ticks"));
		adder.add(dimensionalMirrorUseTime);

		adder.add(new TextWidget(Text.of("Dimensional Mirror Cooldown Time"), this.textRenderer));
		TextFieldWidget dimensionalMirrorCooldownTime = new TextFieldWidget(
			this.textRenderer,
			100,
			20,
			Text.of("Dimensional Mirror Cooldown Time")
		);
		dimensionalMirrorCooldownTime.setChangedListener((value)->config.set("dimensional_mirror_cooldown_time_seconds", value));
		dimensionalMirrorCooldownTime.setText(config.getInteger("dimensional_mirror_cooldown_time_seconds"));
		adder.add(dimensionalMirrorCooldownTime);

		column.add(grid);

		// Mat settings
		column.add(
			CheckboxWidget
			.builder(Text.of("Sleeping mat resets phantom timer"), this.textRenderer)
			.checked(config.getBoolean("sleeping_mat_resets_phantom_timer"))
			.callback((checkbox, checked)->config.set("sleeping_mat_resets_phantom_timer", checked))
			.build()
		);

		this.layout.addBody(column);

		// Config screen buttons
		DirectionalLayoutWidget footer = DirectionalLayoutWidget.horizontal().spacing(8);
		footer.add(ButtonWidget.builder(Text.of("Reset"), (btn) -> this.reset()).dimensions(40, 40, 120, 20).build());
		footer.add(ButtonWidget.builder(Text.of("Done"), (btn) -> this.close()).dimensions(40, 40, 120, 20).build());
		this.layout.addFooter(footer);

		this.layout.forEachChild(child -> {
			this.addDrawableChild(child);
		});
		this.refreshWidgetPositions();
	}

	@Override
	protected void refreshWidgetPositions() {
		this.layout.refreshPositions();
	}

	private void reset() {
		config.setToDefaults();
	}

	@Override
	public void close() {
		config.write();
		this.client.setScreen(this.parent);
	}
}
