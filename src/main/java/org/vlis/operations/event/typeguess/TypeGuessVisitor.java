package org.vlis.operations.event.typeguess;

import org.vlis.operations.event.typeguess.bean.EventBean;

public interface TypeGuessVisitor {
        public void  guess(EventBean eventBean);
}
