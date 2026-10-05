import { Routes } from '@angular/router';

import { Login } from './features/auth/login/login';
import { Register } from './features/auth/register/register';
import { Home } from './features/home/home';
import { authGuard } from './core/guards/auth.guard';
import { Products } from './features/products/products';
import {
    PurchaseHistory
} from './features/purchases/purchase-history/purchase-history';
import {
    AdminProducts
} from './features/admin/admin-products/admin-products';
import {
    adminGuard
} from './core/guards/admin.guard';
import {
    ChangePassword
} from './features/user/change-password/change-password';

import {
    Profile
} from './features/user/profile/profile';

import {
    NotFound
} from './shared/not-found/not-found';

export const routes: Routes = [

    {
        path: '',
        redirectTo: 'login',
        pathMatch: 'full'
    },

    {
        path: 'login',
        component: Login
    },

    {
        path: 'register',
        component: Register
    },

    {
        path: 'home',
        component: Home,
        canActivate: [
            authGuard
        ]
    },

    {
        path: 'products',
        component: Products,
        canActivate: [
            authGuard
        ]
    },

    {
        path: 'purchases',
        component: PurchaseHistory,
        canActivate: [
            authGuard
        ]
    },

    {
        path: 'admin/products',
        component: AdminProducts,
        canActivate: [
            authGuard,
            adminGuard
        ]
    },

    {
        path: 'change-password',
        component: ChangePassword,
        canActivate: [
            authGuard
        ]
    },

    {
        path: 'profile',
        component: Profile,
        canActivate: [
            authGuard
        ]
    },

    {
        path: '**',
        component: NotFound
    }





];