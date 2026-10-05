import {
    PurchaseItem
} from './purchase-item';

export interface PurchaseResponse {

    purchaseId: number;

    purchaseDate: string;

    totalAmount: number;

    items: PurchaseItem[];

    message: string;

}