import {
    HttpInterceptorFn
} from '@angular/common/http';

export const authInterceptor:
    HttpInterceptorFn = (
        request,
        next
    ) => {

        const token =
            localStorage.getItem('token');

        const isPublicRequest =
            request.url.includes(
                '/api/v1/auth/login'
            )
            ||
            request.url.includes(
                '/api/v1/auth/register'
            );

        if (
            token
            &&
            !isPublicRequest
        ) {

            const authenticatedRequest =
                request.clone({

                    setHeaders: {

                        Authorization:
                            `Bearer ${token}`

                    }

                });

            return next(
                authenticatedRequest
            );
        }

        return next(request);
    };
``