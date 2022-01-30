package com.harleyoconnor.gitdesk.ui.repository.issues

import com.harleyoconnor.gitdesk.data.remote.Issue
import com.harleyoconnor.gitdesk.ui.UIResource
import com.harleyoconnor.gitdesk.ui.node.SVGIcon
import com.harleyoconnor.gitdesk.ui.style.CLOSED_PSEUDO_CLASS
import com.harleyoconnor.gitdesk.ui.style.OPEN_PSEUDO_CLASS
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.ui.view.ResourceViewLoader
import com.harleyoconnor.gitdesk.ui.view.ViewController
import com.harleyoconnor.gitdesk.util.xml.SVGCache
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.image.Image
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.ImagePattern
import javafx.scene.shape.Circle
import java.text.SimpleDateFormat

/**
 * @author Harley O'Connor
 */
class IssueViewController : ViewController<IssueViewController.Context> {

    companion object {
        private val CREATED_AT_FORMAT = SimpleDateFormat("dd MMM")
        private val OPEN_ICON = SVGCache.getOrLoad(UIResource("/ui/icons/issue.svg"))
        private val CLOSED_ICON = SVGCache.getOrLoad(UIResource("/ui/icons/closed_issue.svg"))
    }

    object Loader : ResourceViewLoader<Context, IssueViewController, VBox>(
        UIResource("/ui/layouts/repository/issues/IssueView.fxml")
    )

    class Context(val issue: Issue) : ViewController.Context

    private lateinit var issue: Issue

    @FXML
    private lateinit var toggleStateButton: Button

    @FXML
    private lateinit var titleLabel: Label

    @FXML
    private lateinit var numberLabel: Label

    @FXML
    private lateinit var assigneeAvatar: Circle

    @FXML
    private lateinit var subHeadingLabel: Label

    @FXML
    private lateinit var stateBox: HBox

    @FXML
    private lateinit var stateIcon: SVGIcon

    @FXML
    private lateinit var stateLabel: Label

    override fun setup(context: Context) {
        this.issue = context.issue

        titleLabel.text = issue.title
        numberLabel.text = "#${issue.number}"

        issue.assignees.firstOrNull()?.let {
            assigneeAvatar.fill = ImagePattern(Image(it.avatarUrl.toExternalForm()))
        }

        if (issue.state == Issue.State.OPEN) {
            setupUIForOpenIssue()
        } else if (issue.state == Issue.State.CLOSED) {
            setupUIForClosedIssue()
        }
        stateLabel.text = TRANSLATIONS_BUNDLE.getString("issue.state." + issue.state.toString().lowercase())

        subHeadingLabel.text = TRANSLATIONS_BUNDLE.getString("ui.repository.tab.issues.view.subheading")
            .replaceFirst("{}", issue.author.username)
            .replaceFirst("{}", CREATED_AT_FORMAT.format(issue.createdAt))
            .replaceFirst("{}", issue.comments.toString())
    }

    private fun setupUIForOpenIssue() {
        stateBox.pseudoClassStateChanged(OPEN_PSEUDO_CLASS, true)
        toggleStateButton.graphic = getCloseIcon()
        toggleStateButton.tooltip = Tooltip(TRANSLATIONS_BUNDLE.getString("ui.button.close"))
        stateIcon.setupFromSvg(OPEN_ICON)
    }

    private fun setupUIForClosedIssue() {
        stateBox.pseudoClassStateChanged(CLOSED_PSEUDO_CLASS, true)
        toggleStateButton.graphic = getOpenIcon()
        toggleStateButton.tooltip = Tooltip(TRANSLATIONS_BUNDLE.getString("ui.button.open"))
        stateIcon.setupFromSvg(CLOSED_ICON)
    }

    private fun getCloseIcon(): SVGIcon {
        val icon = SVGIcon()
        icon.setPrefSize(12.0, 12.0)
        icon.setupFromSvg(CLOSED_ICON)
        icon.styleClass.addAll("icon", "closed-accent")
        return icon
    }

    private fun getOpenIcon(): SVGIcon {
        val icon = SVGIcon()
        icon.setPrefSize(12.0, 12.0)
        icon.setupFromSvg(OPEN_ICON)
        icon.styleClass.addAll("icon", "open-accent")
        return icon
    }

    @FXML
    private fun toggleState(event: ActionEvent) {

    }

    @FXML
    private fun pin(event: ActionEvent) {

    }

    @FXML
    private fun delete(event: ActionEvent) {

    }


}