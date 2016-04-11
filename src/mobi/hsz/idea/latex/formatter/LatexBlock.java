/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 hsz Jakub Chrzanowski <jakub@hsz.mobi>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package mobi.hsz.idea.latex.formatter;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.TokenType;
import com.intellij.psi.formatter.common.AbstractBlock;
import mobi.hsz.idea.latex.psi.LatexTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * LaTeX {@link Block} implementation.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.3
 */
public class LatexBlock extends AbstractBlock {

    private LatexSpacingProcessor spacingProcessor;

    protected LatexBlock(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment) {
        super(node, wrap, alignment);

//        spacingProcessor = new LatexSpacingProcessor(node);
    }

    @Override
    protected List<Block> buildChildren() {
        List<Block> blocks = new ArrayList<Block>();
        ASTNode child = myNode.getFirstChildNode();
        ASTNode previousChild = null;
        while (child != null) {
            if (child.getElementType() != TokenType.WHITE_SPACE && (previousChild == null || previousChild.getElementType() != LatexTypes.CRLF || child.getElementType() != LatexTypes.CRLF)) {
                Block block = new LatexBlock(child, Wrap.createWrap(WrapType.NONE, false), Alignment.createAlignment());
                blocks.add(block);
            }
            previousChild = child;
            child = child.getTreeNext();
        }
        return blocks;
    }

    @Nullable
    @Override
    public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
        LatexBlock block1 = (LatexBlock) child1;
        LatexBlock block2 = (LatexBlock) child2;

        if (block1 != null && block1.getNode().getElementType() == LatexTypes.COMMENT && block2.getNode().getElementType() == LatexTypes.SECTION) {
            return Spacing.createSpacing(0, 0, 2, false, 10);
        }
        return Spacing.createSpacing(0, 0, 1, false, 0);
    }

    @Override
    public Indent getIndent() {
        return Indent.getNoneIndent();
    }

    @Override
    public boolean isLeaf() {
        return myNode.getFirstChildNode() == null;
    }

}
