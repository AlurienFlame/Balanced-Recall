package net.balancedrecall;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {
    private Screen parent;
	private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
	private final BalancedRecallConfig config;
	
	private CyclingButtonWidget<Boolean> takeDamageInterruptsRecall;
	private CyclingButtonWidget<Boolean> takeDamagePutsMirrorOnCooldown;
	private CyclingButtonWidget<Boolean> recallImpossibleWhenMonstersNearby;
	private TextFieldWidget magicMirrorUseTime;
	private TextFieldWidget magicMirrorCooldownTime;
	private TextFieldWidget dimensionalMirrorUseTime;
	private TextFieldWidget dimensionalMirrorCooldownTime;
	private CyclingButtonWidget<Boolean> sleepingMatResetsPhantomTimer;

	private ButtonWidget resetButton;

	public ConfigScreen(@Nullable Screen parent) {
        super(Text.translatable("balancedrecall.menu.title"));
		this.parent = parent;
		this.config = BalancedRecall.config;
    }

	private void setConfig(String key, Boolean value) {
		config.set(key, value);
		resetButton.active = !config.areDefaults();
	}
	private void setConfig (String key, String value) {
		config.set(key, value);
		resetButton.active = !config.areDefaults();
	}

	@Override
	protected void init() {
		// TODO: detect config in real code

		// Header
		layout.addHeader(new TextWidget(Text.translatable("config.balancedrecall.title"), this.textRenderer));

		// Footer
		DirectionalLayoutWidget footer = DirectionalLayoutWidget.horizontal().spacing(8);
		resetButton = ButtonWidget.builder(Text.translatable("controls.reset"), (btn) -> this.reset()).dimensions(40, 40, 120, 20).build();
		footer.add(resetButton);
		footer.add(ButtonWidget.builder(ScreenTexts.DONE, (btn) -> this.close()).dimensions(40, 40, 120, 20).build());
		this.layout.addFooter(footer);

		// Body
		DirectionalLayoutWidget body = DirectionalLayoutWidget.vertical().spacing(8);

		// Mirror settings
		takeDamageInterruptsRecall = CyclingButtonWidget
			.onOffBuilder()
			.initially(config.getBoolean("take_damage_interrupts_recall"))
			.build(Text.translatable("config.balancedrecall.take_damage_interrupts_recall"), (button, value)->setConfig("take_damage_interrupts_recall", value));
		body.add(takeDamageInterruptsRecall);

		takeDamagePutsMirrorOnCooldown = CyclingButtonWidget
			.onOffBuilder()
			.initially(config.getBoolean("take_damage_puts_mirror_on_cooldown"))
			.build(Text.translatable("config.balancedrecall.take_damage_puts_mirror_on_cooldown"), (button, value)->setConfig("take_damage_puts_mirror_on_cooldown", value));
		body.add(takeDamagePutsMirrorOnCooldown);

		recallImpossibleWhenMonstersNearby = CyclingButtonWidget
			.onOffBuilder()
			.initially(config.getBoolean("recall_impossible_when_monsters_nearby"))
			.build(Text.translatable("config.balancedrecall.recall_impossible_when_monsters_nearby"), (button, value)->setConfig("recall_impossible_when_monsters_nearby", value));
		body.add(recallImpossibleWhenMonstersNearby);

		GridWidget grid = new GridWidget();
		grid.getMainPositioner().margin(4).alignHorizontalCenter();
		GridWidget.Adder adder = grid.createAdder(2);
		adder.getMainPositioner().alignLeft().alignVerticalCenter();

		adder.add(new TextWidget(Text.translatable("config.balancedrecall.magic_mirror_use_time_ticks"), this.textRenderer));
		magicMirrorUseTime = new TextFieldWidget(
			this.textRenderer,
			100,
			20,
			Text.translatable("config.balancedrecall.magic_mirror_use_time_ticks")
		);
		magicMirrorUseTime.setChangedListener((value)->setConfig("magic_mirror_use_time_ticks", value));
		magicMirrorUseTime.setText(config.getInteger("magic_mirror_use_time_ticks"));
		adder.add(magicMirrorUseTime);

		adder.add(new TextWidget(Text.translatable("config.balancedrecall.magic_mirror_cooldown_time_seconds"), this.textRenderer));
		magicMirrorCooldownTime = new TextFieldWidget(
			this.textRenderer,
			100,
			20,
			Text.translatable("config.balancedrecall.magic_mirror_cooldown_time_seconds")
		);
		magicMirrorCooldownTime.setChangedListener((value)->setConfig("magic_mirror_cooldown_time_seconds", value));
		magicMirrorCooldownTime.setText(config.getInteger("magic_mirror_cooldown_time_seconds"));
		adder.add(magicMirrorCooldownTime);

		adder.add(new TextWidget(Text.translatable("config.balancedrecall.dimensional_mirror_use_time_ticks"), this.textRenderer));
		dimensionalMirrorUseTime = new TextFieldWidget(
			this.textRenderer,
			100,
			20,
			Text.translatable("config.balancedrecall.dimensional_mirror_use_time_ticks")
		);
		dimensionalMirrorUseTime.setChangedListener((value)->setConfig("dimensional_mirror_use_time_ticks", value));
		dimensionalMirrorUseTime.setText(config.getInteger("dimensional_mirror_use_time_ticks"));
		adder.add(dimensionalMirrorUseTime);

		adder.add(new TextWidget(Text.translatable("config.balancedrecall.dimensional_mirror_cooldown_time_seconds"), this.textRenderer));
		dimensionalMirrorCooldownTime = new TextFieldWidget(
			this.textRenderer,
			100,
			20,
			Text.translatable("config.balancedrecall.dimensional_mirror_cooldown_time_seconds")
		);
		dimensionalMirrorCooldownTime.setChangedListener((value)->setConfig("dimensional_mirror_cooldown_time_seconds", value));
		dimensionalMirrorCooldownTime.setText(config.getInteger("dimensional_mirror_cooldown_time_seconds"));
		adder.add(dimensionalMirrorCooldownTime);

		body.add(grid);

		// Mat settings
		sleepingMatResetsPhantomTimer = CyclingButtonWidget
			.onOffBuilder()
			.initially(config.getBoolean("sleeping_mat_resets_phantom_timer"))
			.build(Text.translatable("config.balancedrecall.sleeping_mat_resets_phantom_timer"), (button, value)->setConfig("sleeping_mat_resets_phantom_timer", value));
		body.add(sleepingMatResetsPhantomTimer);
		this.layout.addBody(body);

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
		takeDamageInterruptsRecall.setValue(config.getBoolean("take_damage_interrupts_recall"));
		takeDamagePutsMirrorOnCooldown.setValue(config.getBoolean("take_damage_puts_mirror_on_cooldown"));
		recallImpossibleWhenMonstersNearby.setValue(config.getBoolean("recall_impossible_when_monsters_nearby"));
		magicMirrorUseTime.setText(config.getInteger("magic_mirror_use_time_ticks"));
		magicMirrorCooldownTime.setText(config.getInteger("magic_mirror_cooldown_time_seconds"));
		dimensionalMirrorUseTime.setText(config.getInteger("dimensional_mirror_use_time_ticks"));
		dimensionalMirrorCooldownTime.setText(config.getInteger("dimensional_mirror_cooldown_time_seconds"));
		sleepingMatResetsPhantomTimer.setValue(config.getBoolean("sleeping_mat_resets_phantom_timer"));
		resetButton.active = !config.areDefaults();
	}

	@Override
	public void close() {
		config.write();
		this.client.setScreen(this.parent);
	}
}
