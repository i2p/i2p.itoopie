package net.i2p.itoopie.gui.component;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.RootPaneContainer;
import javax.swing.UIManager;

public final class ProgressiveDisclosurePanel extends JOptionPane {
    /** Label. */
    private final JLabel label;

    /** Wrapped container. */
    private final Container container;

    /** True if this disclosure triangle container is collapsed. */
    private boolean collapsed;

    /** Collapse icon. */
    private Icon collapseIcon;

    /** Expand icon. */
    private Icon expandIcon;

    /** Default label text, <code>"Details"</code>. */
    public static final String DEFAULT_LABEL_TEXT = "Details";

    /**
     * Create a new disclosure triangle container wrapping the specified
     * container.
     * 
     * <p>
     * By default this disclosure triangle container will be collapsed, its label
     * text will be <code>"Details"</code>, its collapse icon will be the icon
     * resource returned by <code>UIManager.getIcon("Tree.expandedIcon")</code>,
     * and its expand icon will be the icon resource returned by
     * <code>UIManager.getIcon("Tree.collapsedIcon")</code>.
     * </p>
     * 
     * @see #DEFAULT_LABEL_TEXT
     * @see javax.swing.UIManager#getIcon
     * @param container
     *          container to wrap, must not be null
     */
    public ProgressiveDisclosurePanel(final Container container) {
        super ();
        if (container == null) {
            throw new IllegalArgumentException(
                    "container must not be null");
        }
        this .container = container;

        collapsed = true;
        collapseIcon = UIManager.getIcon("Tree.expandedIcon");
        expandIcon = UIManager.getIcon("Tree.collapsedIcon");
        label = new JLabel(DEFAULT_LABEL_TEXT, expandIcon,
                JLabel.LEADING);

        label.addMouseListener(new MouseAdapter() {
            /** {@inheritDoc} */
            public void mouseClicked(final MouseEvent event) {
                if (collapsed) {
                    expand();
                } else {
                    collapse();
                }
            }
        });

        setLayout(new BorderLayout());
        add("North", label);
    }

    /**
     * Expand this disclosure triangle container.
     */
    public void expand() {
        setCollapsed(false);
    }

    /**
     * Actually perform the expand operation.
     */
    private void doExpand() {
        add("Center", container);
        label.setIcon(collapseIcon);
        Container rootPaneContainer = getParentRootPaneContainer();
        if (rootPaneContainer != null) {
            Dimension dim0 = rootPaneContainer.getSize();
            Dimension dim1 = container.getSize();
            Dimension dim2 = container.getPreferredSize();
            rootPaneContainer.setSize(dim0.width, dim0.height + Math.max(dim1.height, dim2.height));
        }
    }

    /**
     * Collapse this disclosure triangle container.
     */
    public void collapse() {
        setCollapsed(true);
    }

    /**
     * Actually perform the collapse operation.
     */
    private void doCollapse() {
        remove(container);
        label.setIcon(expandIcon);
        Container rootPaneContainer = getParentRootPaneContainer();
        if (rootPaneContainer != null) {
            Dimension d0 = rootPaneContainer.getSize();
            Dimension d1 = container.getSize();
            Dimension d2 = container.getPreferredSize();
            rootPaneContainer.setSize(d0.width, d0.height
                    - Math.max(d1.height, d2.height));
        }
    }

    /**
     * Return true if this disclosure triangle container is collapsed.
     * 
     * @return true if this disclosure triangle container is collapsed
     */
    public boolean isCollapsed() {
        return collapsed;
    }

    /**
     * Set to true to collapse this disclosure triangle container. Alternatively,
     * call <code>collapse()</code> or <code>expand()</code> as appropriate.
     * 
     * <p>
     * This is a bound property.
     * </p>
     * 
     * @param collapsed true to collapse this disclosure triangle container
     */
    public void setCollapsed(final boolean collapsed) {
        boolean oldCollapsed = this .collapsed;
        if (collapsed && !oldCollapsed) {
            doCollapse();
        }
        if (!collapsed && oldCollapsed) {
            doExpand();
        }
        this .collapsed = collapsed;
        firePropertyChange("collapsed", oldCollapsed, this .collapsed);
    }

    /**
     * Return the parent root pane container for this disclosure triangle
     * container or null if one does not exist.
     * 
     * @return the parent root pane container for this disclosure triangle
     *         container or null if one does not exist
     */
    private Container getParentRootPaneContainer() {
        Container c = this ;
        while (!(c instanceof  RootPaneContainer)) {
            if (c.getParent() == null) {
                return null;
            }
            c = c.getParent();
        }
        return c;
    }

    /**
     * Return the label text for this disclosure triangle container.
     * 
     * @return the label text for this disclosure triangle container
     */
    public String getLabelText() {
        return label.getText();
    }

    /**
     * Set the label text for this disclosure triangle container to
     * <code>labelText</code>.
     * 
     * <p>
     * This is a bound property.
     * </p>
     * 
     * @param labelText label text for this disclosure triangle container
     */
    public void setLabelText(final String labelText) {
        String oldLabelText = label.getText();
        label.setText(labelText);
        firePropertyChange("labelText", oldLabelText, label.getText());
    }

    /**
     * Return the collapse icon for this disclosure triangle container.
     * 
     * @return the collapse icon for this disclosure triangle container
     */
    public Icon getCollapseIcon() {
        return collapseIcon;
    }

    /**
     * Set the collapse icon for this disclosure triangle container to
     * <code>collapseIcon</code>.
     * 
     * <p>
     * This is a bound property.
     * </p>
     * 
     * @param collapseIcon
     *          collapse icon for this disclosure triangle container
     */
    public void setCollapseIcon(final Icon collapseIcon) {
        Icon oldCollapseIcon = this .collapseIcon;
        this .collapseIcon = collapseIcon;
        firePropertyChange("collapseIcon", oldCollapseIcon,
                this .collapseIcon);
        if (!collapsed) {
            label.setIcon(this .collapseIcon);
        }
    }

    /**
     * Return the expand icon for this disclosure triangle container.
     * 
     * @return the expand icon for this disclosure triangle container
     */
    public Icon getExpandIcon() {
        return expandIcon;
    }

    /**
     * Set the expand icon for this disclosure triangle container to
     * <code>expandIcon</code>.
     * 
     * <p>
     * This is a bound property.
     * </p>
     * 
     * @param expandIcon expand icon for this disclosure triangle container
     */
    public void setExpandIcon(final Icon expandIcon) {
        Icon oldExpandIcon = this .expandIcon;
        this.expandIcon = expandIcon;
        firePropertyChange("expandIcon", oldExpandIcon, this.expandIcon);
        if (collapsed) {
            label.setIcon(this.expandIcon);
        }
    }
}
