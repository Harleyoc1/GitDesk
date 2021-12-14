package com.harleyoconnor.gitdesk.data

import com.harleyoconnor.gitdesk.data.local.RepositoryAccess
import com.harleyoconnor.gitdesk.data.syntax.SyntaxHighlighterAccess

/**
 * @author Harley O'Connor
 */
interface DataAccessor {

    val repositoryAccess: RepositoryAccess

    val syntaxHighlighterAccess: SyntaxHighlighterAccess

}