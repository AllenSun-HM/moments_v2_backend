package com.allen.moments.v2.utils.error_handler;

import com.allen.moments.v2.model.ErrorType;
import com.allen.moments.v2.utils.ApplicationException;

public class DBCheckedExceptionHandler {
    public static void checkIfRowsAffectedIsOne(int rowsAffected, ErrorType zeroRowAffectedErrorType) throws Exception {
        if (rowsAffected == 0) {
            throw new ApplicationException(zeroRowAffectedErrorType.errNo, zeroRowAffectedErrorType.message);
        }
        if (rowsAffected > 1) {
            throw new ApplicationException(ErrorType.DIRTY_DATA.errNo, ErrorType.DIRTY_DATA.message);
        }
    }
}
