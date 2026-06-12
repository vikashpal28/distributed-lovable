import { useState, useEffect } from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Loader2, Check } from "lucide-react";
import { api } from "@/lib/api";
import { PlanResponse } from "@/lib/types";
import { useToast } from "@/hooks/use-toast";

export function PlansDialog({ open, onOpenChange }: { open: boolean; onOpenChange: (open: boolean) => void }) {
  const { toast } = useToast();
  const [plans, setPlans] = useState<PlanResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [checkoutLoadingId, setCheckoutLoadingId] = useState<number | null>(null);

  useEffect(() => {
    if (open) {
      fetchPlans();
    }
  }, [open]);

  const fetchPlans = async () => {
    setLoading(true);
    try {
      // Set hardcoded plans as requested
      const defaultPlans: PlanResponse[] = [
        {
          id: 1,
          name: "Pro Plan",
          price: "299",
          maxProducts: 3,
          maxTokenPerDay: 30000,
          unlimitedAi: false
        },
        {
          id: 2,
          name: "Business Plan",
          price: "1499",
          maxProducts: 10,
          maxTokenPerDay: 80000,
          unlimitedAi: true
        }
      ];
      setPlans(defaultPlans);
    } catch (error) {
      console.error("Failed to load plans:", error);
      toast({
        title: "Error",
        description: "Failed to load plans.",
        variant: "destructive",
      });
    } finally {
      setLoading(false);
    }
  };

  const handleCheckout = async (planId: number) => {
    setCheckoutLoadingId(planId);
    try {
      const successUrl = `${window.location.origin}/success`;
      const cancelUrl = `${window.location.origin}/cancel`;
      const response = await api.createCheckoutSession(planId, successUrl, cancelUrl);
      if (response.checkoutUrl) {
        window.location.href = response.checkoutUrl;
      }
    } catch (error) {
      console.error("Failed to create checkout session:", error);
      toast({
        title: "Error",
        description: "Failed to initiate checkout.",
        variant: "destructive",
      });
    } finally {
      setCheckoutLoadingId(null);
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-3xl">
        <DialogHeader>
          <DialogTitle>AI Subscription Plans</DialogTitle>
          <DialogDescription>
            Choose a plan that fits your needs to unlock more projects and AI capabilities.
          </DialogDescription>
        </DialogHeader>
        <div className="py-4">
          {loading ? (
            <div className="flex justify-center items-center py-10">
              <Loader2 className="w-8 h-8 animate-spin text-primary" />
            </div>
          ) : (
            <div className="flex flex-col md:flex-row justify-center items-stretch gap-6">
              {plans.map((plan) => (
                <div key={plan.id} className="flex-1 max-w-[320px] w-full border rounded-xl p-6 flex flex-col hover:border-primary/50 transition-colors shadow-sm">
                  <h3 className="text-xl font-bold">{plan.name}</h3>
                  <div className="mt-4 mb-6">
                    <span className="text-3xl font-bold">₹{plan.price}</span>
                  </div>
                  <ul className="space-y-3 mb-8 flex-1 text-sm text-muted-foreground">
                    <li className="flex items-center gap-2">
                      <Check className="w-4 h-4 text-green-500" />
                      {plan.maxProducts} Projects
                    </li>
                    <li className="flex items-center gap-2">
                      <Check className="w-4 h-4 text-green-500" />
                      {plan.maxTokenPerDay} Tokens / Day
                    </li>
                    <li className="flex items-center gap-2">
                      <Check className="w-4 h-4 text-green-500" />
                      {plan.unlimitedAi ? "Unlimited AI" : "Standard AI"}
                    </li>
                  </ul>
                  <Button 
                    className="w-full" 
                    onClick={() => handleCheckout(plan.id)}
                    disabled={checkoutLoadingId === plan.id}
                  >
                    {checkoutLoadingId === plan.id && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                    Subscribe
                  </Button>
                </div>
              ))}
              {plans.length === 0 && !loading && (
                <div className="col-span-full text-center py-10 text-muted-foreground">
                  No plans available at the moment.
                </div>
              )}
            </div>
          )}
        </div>
      </DialogContent>
    </Dialog>
  );
}
