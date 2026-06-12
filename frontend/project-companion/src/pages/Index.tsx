import { useState, useEffect } from "react";
import { useNavigate, Link, useSearchParams } from "react-router-dom";
import { ChevronDown, Plus, Mic, ArrowUp, ArrowRight } from "lucide-react";

const placeholders = [
  "Ask Lovable to create a personal website",
  "Ask Lovable to create a python script",
  "Ask Lovable to create a landing page",
  "Ask Lovable to create a dashboard",
  "Ask Lovable to create a web app"
];

const Index = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  useEffect(() => {
    if (searchParams.get("redirect") === "projects") {
      navigate("/projects", { replace: true });
    }
  }, [searchParams, navigate]);
  const [placeholderText, setPlaceholderText] = useState("");
  const [phIndex, setPhIndex] = useState(0);
  const [charIndex, setCharIndex] = useState(0);
  const [isDeleting, setIsDeleting] = useState(false);

  useEffect(() => {
    const typingSpeed = isDeleting ? 50 : 100;
    const currentString = placeholders[phIndex];

    const timeout = setTimeout(() => {
      if (!isDeleting && charIndex < currentString.length) {
        setPlaceholderText(currentString.substring(0, charIndex + 1));
        setCharIndex((prev) => prev + 1);
      } else if (isDeleting && charIndex > 0) {
        setPlaceholderText(currentString.substring(0, charIndex - 1));
        setCharIndex((prev) => prev - 1);
      } else if (!isDeleting && charIndex === currentString.length) {
        setTimeout(() => setIsDeleting(true), 1500); // Pause before deleting
      } else if (isDeleting && charIndex === 0) {
        setIsDeleting(false);
        setPhIndex((prev) => (prev + 1) % placeholders.length);
      }
    }, typingSpeed);

    return () => clearTimeout(timeout);
  }, [charIndex, isDeleting, phIndex]);

  return (
    <div className="min-h-screen bg-[#0a0a0b] text-white flex flex-col relative overflow-hidden font-sans">
      {/* Vibrant background gradient effect */}
      <div className="absolute inset-0 z-0 opacity-80 pointer-events-none overflow-hidden">
         {/* Blobs to create the gradient mesh */}
         <div className="absolute top-[-10%] right-[-5%] w-[70vw] h-[70vw] rounded-full bg-[radial-gradient(circle,rgba(56,98,255,0.7)_0%,rgba(0,0,0,0)_60%)] blur-[100px] mix-blend-screen" />
         <div className="absolute bottom-[-10%] left-[-10%] w-[80vw] h-[80vw] rounded-full bg-[radial-gradient(circle,rgba(255,30,120,0.6)_0%,rgba(0,0,0,0)_60%)] blur-[120px] mix-blend-screen" />
         <div className="absolute bottom-[-20%] right-[-10%] w-[60vw] h-[60vw] rounded-full bg-[radial-gradient(circle,rgba(255,100,0,0.6)_0%,rgba(0,0,0,0)_60%)] blur-[120px] mix-blend-screen" />
      </div>

      {/* Header */}
      <header className="relative z-10 flex items-center justify-between px-6 py-4">
        <div className="flex items-center gap-8">
          {/* Logo */}
          <Link to="/" className="flex items-center gap-2 font-bold text-xl tracking-tight">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" className="text-transparent">
              <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z" fill="url(#paint0_linear)"/>
              <defs>
                <linearGradient id="paint0_linear" x1="2" y1="3" x2="22" y2="21.35" gradientUnits="userSpaceOnUse">
                  <stop stopColor="#FF6B6B"/>
                  <stop offset="1" stopColor="#FF8E53"/>
                </linearGradient>
              </defs>
            </svg>
            Lovable
          </Link>
          
          {/* Nav */}
          <nav className="hidden md:flex items-center gap-6 text-sm font-medium text-gray-300">
            <button className="flex items-center gap-1 hover:text-white transition-colors">Solutions <ChevronDown className="w-3 h-3 opacity-70" /></button>
            <button className="flex items-center gap-1 hover:text-white transition-colors">Resources <ChevronDown className="w-3 h-3 opacity-70" /></button>
            <Link to="#" className="hover:text-white transition-colors">Community</Link>
            <Link to="#" className="hover:text-white transition-colors">Enterprise</Link>
            <Link to="#" className="hover:text-white transition-colors">Pricing</Link>
            <Link to="#" className="hover:text-white transition-colors">Security</Link>
          </nav>
        </div>

        <div className="flex items-center gap-4">
          <Link to="/login" className="text-sm font-medium text-white hover:text-gray-300 transition-colors">Log In</Link>
          <Link to="/signup" className="text-sm font-medium bg-white text-black px-4 py-2 rounded-md hover:bg-gray-100 transition-colors">Get started</Link>
        </div>
      </header>

      {/* Main Content */}
      <main className="relative z-10 flex-1 flex flex-col items-center justify-center px-4 pt-10 pb-32">
        
        {/* Badge */}
        <div className="flex items-center gap-2 bg-black/40 backdrop-blur-md border border-white/10 rounded-full pl-1.5 pr-4 py-1.5 mb-8 hover:bg-black/60 transition-colors cursor-pointer text-sm shadow-xl">
          <span className="bg-blue-600 text-white text-xs font-semibold px-2.5 py-0.5 rounded-full">New</span>
          <span className="text-gray-200 font-medium">Better SEO – Apps built to be found</span>
          <ArrowRight className="w-4 h-4 text-gray-400 ml-1" />
        </div>

        <h1 className="text-5xl md:text-[64px] font-bold tracking-tight text-white mb-6 text-center animate-in fade-in slide-in-from-bottom-4 duration-1000">
          Build something Lovable
        </h1>
        
        <p className="text-lg md:text-xl text-gray-300 mb-12 text-center max-w-2xl animate-in fade-in slide-in-from-bottom-4 duration-1000 delay-150">
          Create apps and websites by chatting with AI
        </p>

        {/* Input Block */}
        <div className="w-full max-w-[800px] bg-[#1a1a1a]/90 backdrop-blur-xl border border-white/5 rounded-2xl p-4 shadow-2xl flex flex-col min-h-[140px] justify-between animate-in fade-in slide-in-from-bottom-8 duration-1000 delay-300">
          <div className="flex items-start gap-3 w-full mb-4">
            <input 
              type="text" 
              placeholder={placeholderText || " "}
              className="flex-1 bg-transparent border-none outline-none text-base text-white placeholder:text-gray-500 pt-1 px-2"
            />
          </div>
          
          <div className="flex items-center justify-between mt-auto">
            <button className="w-8 h-8 rounded-full bg-white/5 flex items-center justify-center hover:bg-white/10 transition-colors ml-2">
              <Plus className="w-4 h-4 text-gray-400" />
            </button>
            
            <div className="flex items-center gap-3">
              <button className="flex items-center gap-1 text-xs font-medium text-gray-400 hover:text-white transition-colors bg-white/5 px-3 py-1.5 rounded-md">
                Build <ChevronDown className="w-3 h-3 ml-0.5" />
              </button>
              <button className="w-8 h-8 flex items-center justify-center text-gray-400 hover:text-white transition-colors">
                <Mic className="w-4 h-4" />
              </button>
              <button className="w-8 h-8 rounded-full bg-white/20 flex items-center justify-center hover:bg-white/30 transition-colors text-white mr-1">
                <ArrowUp className="w-4 h-4" />
              </button>
            </div>
          </div>
        </div>

      </main>
    </div>
  );
};

export default Index;
