package net.balancedrecall;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class ConfigScreen extends Screen {
    private Screen parent;
	private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
	private final BalancedRecallConfig config;
	
	private CycleButton<Boolean> takeDamageInterruptsRecall;
	private CycleButton<Boolean> takeDamagePutsMirrorOnCooldown;
	private CycleButton<Boolean> recallImpossibleWhenMonstersNearby;
	private EditBox magicMirrorUseTime;
	private EditBox magicMirrorCooldownTime;
	private EditBox dimensionalMirrorUseTime;
	private EditBox dimensionalMirrorCooldownTime;
	private CycleButton<Boolean> sleepingMatResetsPhantomTimer;

	private Button resetButton;

	public ConfigScreen(@Nullable Screen parent) {
        super(Component.translatable("balancedrecall.menu.title"));
		this.parent = parent;
		this.config = BalancedRecall.config;
    }

	private void setConfig(String key, Boolean value) {
		config.set(key, value);
		refreshWidgetActiveness();
	}
	private void setConfig (String key, String value) {
		config.set(key, value);
		refreshWidgetActiveness();
	}

	@Override
	protected void init() {
		// Header
		layout.addToHeader(new StringWidget(Component.translatable("config.balancedrecall.title"), this.font));

		// Footer
		LinearLayout footer = LinearLayout.horizontal().spacing(8);
		resetButton = Button.builder(Component.translatable("controls.reset"), (btn) -> this.reset()).bounds(40, 40, 120, 20).build();
		footer.addChild(resetButton);
		footer.addChild(Button.builder(CommonComponents.GUI_DONE, (btn) -> this.onClose()).bounds(40, 40, 120, 20).build());
		this.layout.addToFooter(footer);

		// Body
		LinearLayout body = LinearLayout.vertical().spacing(8);

		// Mirror settings
		takeDamageInterruptsRecall = CycleButton
			.onOffBuilder(BalancedRecallConfig.DEFAULT_TAKE_DAMAGE_INTERRUPTS_RECALL)
			.create(0, 0, 300, 20, Component.translatable("config.balancedrecall.take_damage_interrupts_recall"), (button, value)->setConfig("take_damage_interrupts_recall", value));
		body.addChild(takeDamageInterruptsRecall);

		takeDamagePutsMirrorOnCooldown = CycleButton
			.onOffBuilder(BalancedRecallConfig.DEFAULT_TAKE_DAMAGE_PUTS_MIRROR_ON_COOLDOWN)
			.create(0, 0, 300, 20, Component.translatable("config.balancedrecall.take_damage_puts_mirror_on_cooldown"), (button, value)->setConfig("take_damage_puts_mirror_on_cooldown", value));
		body.addChild(takeDamagePutsMirrorOnCooldown);

		recallImpossibleWhenMonstersNearby = CycleButton
			.onOffBuilder(BalancedRecallConfig.DEFAULT_RECALL_IMPOSSIBLE_WHEN_MONSTERS_NEARBY)
			.create(0, 0, 300, 20, Component.translatable("config.balancedrecall.recall_impossible_when_monsters_nearby"), (button, value)->setConfig("recall_impossible_when_monsters_nearby", value));
		body.addChild(recallImpossibleWhenMonstersNearby);

		GridLayout grid = new GridLayout();
		grid.defaultCellSetting().padding(4).alignHorizontallyCenter();
		GridLayout.RowHelper adder = grid.createRowHelper(2);
		adder.defaultCellSetting().alignHorizontallyLeft().alignVerticallyMiddle();

		adder.addChild(new StringWidget(Component.translatable("config.balancedrecall.magic_mirror_use_time_seconds"), this.font));
		magicMirrorUseTime = new EditBox(
			this.font,
			100,
			20,
			Component.translatable("config.balancedrecall.magic_mirror_use_time_seconds")
		);
		magicMirrorUseTime.setResponder((value)->setConfig("magic_mirror_use_time_seconds", value));
		adder.addChild(magicMirrorUseTime);

		adder.addChild(new StringWidget(Component.translatable("config.balancedrecall.dimensional_mirror_use_time_seconds"), this.font));
		dimensionalMirrorUseTime = new EditBox(
			this.font,
			100,
			20,
			Component.translatable("config.balancedrecall.dimensional_mirror_use_time_seconds")
		);
		dimensionalMirrorUseTime.setResponder((value)->setConfig("dimensional_mirror_use_time_seconds", value));
		adder.addChild(dimensionalMirrorUseTime);

		adder.addChild(new StringWidget(Component.translatable("config.balancedrecall.magic_mirror_cooldown_time_seconds"), this.font));
		magicMirrorCooldownTime = new EditBox(
			this.font,
			100,
			20,
			Component.translatable("config.balancedrecall.magic_mirror_cooldown_time_seconds")
		);
		magicMirrorCooldownTime.setResponder((value)->setConfig("magic_mirror_cooldown_time_seconds", value));
		adder.addChild(magicMirrorCooldownTime);

		adder.addChild(new StringWidget(Component.translatable("config.balancedrecall.dimensional_mirror_cooldown_time_seconds"), this.font));
		dimensionalMirrorCooldownTime = new EditBox(
			this.font,
			100,
			20,
			Component.translatable("config.balancedrecall.dimensional_mirror_cooldown_time_seconds")
		);
		dimensionalMirrorCooldownTime.setResponder((value)->setConfig("dimensional_mirror_cooldown_time_seconds", value));
		adder.addChild(dimensionalMirrorCooldownTime);

		body.addChild(grid);

		body.addChild(new StringWidget(Component.translatable("config.balancedrecall.restart_necessary"), this.font));

		// Mat settings
		sleepingMatResetsPhantomTimer = CycleButton
			.onOffBuilder(BalancedRecallConfig.DEFAULT_SLEEPING_MAT_RESETS_PHANTOM_TIMER)
			.create(0, 0, 300, 20, Component.translatable("config.balancedrecall.sleeping_mat_resets_phantom_timer"), (button, value)->setConfig("sleeping_mat_resets_phantom_timer", value));
		body.addChild(sleepingMatResetsPhantomTimer);
		this.layout.addToContents(body);

		this.layout.visitWidgets(child -> {
			this.addRenderableWidget(child);
		});
		this.repositionElements();
		refreshWidgetValues();
	}

	@Override
	protected void repositionElements() {
		this.layout.arrangeElements();
	}

	private void reset() {
		config.setToDefaults();
		refreshWidgetValues();
	}

	private void refreshWidgetValues() {
		takeDamageInterruptsRecall.setValue(config.getBoolean("take_damage_interrupts_recall"));
		takeDamagePutsMirrorOnCooldown.setValue(config.getBoolean("take_damage_puts_mirror_on_cooldown"));
		recallImpossibleWhenMonstersNearby.setValue(config.getBoolean("recall_impossible_when_monsters_nearby"));
		// TODO: Find a way to scale the model animation dynamically with the use time
		// (I'm pretty sure this is not possible at runtime sadly)
		magicMirrorUseTime.setValue(config.getDouble("magic_mirror_use_time_seconds").toString());
		magicMirrorCooldownTime.setValue(config.getDouble("magic_mirror_cooldown_time_seconds").toString());
		dimensionalMirrorUseTime.setValue(config.getDouble("dimensional_mirror_use_time_seconds").toString());
		dimensionalMirrorCooldownTime.setValue(config.getDouble("dimensional_mirror_cooldown_time_seconds").toString());
		sleepingMatResetsPhantomTimer.setValue(config.getBoolean("sleeping_mat_resets_phantom_timer"));
		refreshWidgetActiveness();
	}


	private void refreshWidgetActiveness() {
		resetButton.active = !config.areDefaults();

		// This option can only be true if the other one is, otherwise the button is disabled.
		takeDamagePutsMirrorOnCooldown.active = config.getBoolean("take_damage_interrupts_recall");
		if (!config.getBoolean("take_damage_interrupts_recall") && takeDamagePutsMirrorOnCooldown.getValue()) {
			takeDamagePutsMirrorOnCooldown.setValue(false);
			setConfig("take_damage_puts_mirror_on_cooldown", false);
		}
	}

	@Override
	public void onClose() {
		config.write();
		this.minecraft.gui.setScreen(this.parent);
	}
}
