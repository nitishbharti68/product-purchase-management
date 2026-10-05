export interface Product {

    productId: number;

    productName: string;

    description: string;

    price: number;

    quantity: number;

    category: string | null;

    imagePath: string | null;

    active: boolean;

    createdDate: string;

}   